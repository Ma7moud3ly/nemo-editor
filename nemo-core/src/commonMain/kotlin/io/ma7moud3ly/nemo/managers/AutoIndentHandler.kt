package io.ma7moud3ly.nemo.managers

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.ma7moud3ly.nemo.InternalNemoApi
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.Language

/**
 * Handles automatic indentation when user presses Enter
 */
@InternalNemoApi
class AutoIndentHandler(
    private val language: Language,
    private val settings: EditorSettings
) {
    /**
     * Process text change and add auto-indent if needed
     * @return TextFieldValue with auto-indent applied, or null if no auto-indent needed
     */
    fun handleTextChange(
        oldValue: TextFieldValue,
        newValue: TextFieldValue
    ): TextFieldValue? {
        // Work out the edit from the text itself. Reading it off the reported
        // caret breaks on input methods and on the web backend, where the caret
        // still points at the previous offset when the change arrives.
        val edit = diffText(oldValue.text, newValue.text) ?: return null

        // Only a bare Enter indents - a paste that happens to contain a newline
        // must keep the text exactly as pasted.
        if (edit.removed.isNotEmpty()) return null
        if (edit.inserted != "\n" && edit.inserted != "\r\n") return null

        // The line break this Enter should have made, in the text before it
        val text = oldValue.text
        val (inserted, caret) = lineBreakAt(text, edit.start)
        if (inserted == "\n") return null

        return TextFieldValue(
            text = text.substring(0, edit.start) + inserted + text.substring(edit.start),
            selection = TextRange(edit.start + caret)
        )
    }

    /**
     * Indentation a line break inserted at [position] should carry, taken from
     * the line the caret is leaving and widened by one level when that line
     * opens a block.
     *
     * Returns an empty string when the new line belongs at column zero.
     */
    fun indentForLineBreakAt(text: String, position: Int): String {
        val caret = position.coerceIn(0, text.length)
        val currentLine = text.take(caret).substringAfterLast('\n')
        return calculateNewIndent(
            baseIndent = getLineIndent(currentLine),
            shouldIncrease = shouldIncreaseIndent(currentLine)
        )
    }

    /**
     * The text a line break replacing [start] to [end] of [text] inserts, and
     * where the caret goes in it.
     *
     * It is a newline and the indentation of the new line. When the caret sits
     * between a bracket and the one that closes it, as in `Column {|}`, a
     * second line break moves the closing bracket to its own line at the
     * indentation of the line being left, and the caret goes on the indented
     * line between them.
     */
    fun lineBreakAt(text: String, start: Int, end: Int = start): Pair<String, Int> {
        val caret = start.coerceIn(0, text.length)
        val currentLine = text.take(caret).substringAfterLast('\n')
        val opening = currentLine.trimEnd().lastOrNull()
        val closing = text.getOrNull(end.coerceIn(caret, text.length))

        if (opening != null && closing != null && CLOSING_BRACKETS[opening] == closing &&
            shouldIncreaseIndent(currentLine)
        ) {
            val baseIndent = getLineIndent(currentLine)
            val inner = "\n" + calculateNewIndent(baseIndent, shouldIncrease = true)
            return inner + "\n" + baseIndent to inner.length
        }

        val inserted = "\n" + indentForLineBreakAt(text, caret)
        return inserted to inserted.length
    }

    private fun getLineIndent(line: String): String {
        return line.takeWhile { it == ' ' || it == '\t' }
    }

    private fun shouldIncreaseIndent(line: String): Boolean {
        val trimmedLine = line.trimEnd()

        return when (language) {
            Language.KOTLIN -> shouldIncreaseIndentKotlin(trimmedLine)
            Language.PYTHON, Language.MICRO_PYTHON -> shouldIncreaseIndentPython(trimmedLine)
            Language.JAVA -> shouldIncreaseIndentJava(trimmedLine)
            Language.JAVASCRIPT -> shouldIncreaseIndentJavaScript(trimmedLine)
            else -> false
        }
    }

    private fun shouldIncreaseIndentKotlin(line: String): Boolean {
        // Increase indent after an opening bracket, as in `Column {` or `Column(`
        if (line.endsWithOpeningBracket()) return true

        // Increase indent after = in expressions (but not in parameter lists)
        if (line.endsWith("=") && !line.contains("(")) return true

        // Increase indent after -> in when expressions
        if (line.endsWith("->")) return true

        // Increase indent after colon in labels
        if (line.matches(Regex(".*\\w+:\\s*"))) return true

        return false
    }

    private fun shouldIncreaseIndentPython(line: String): Boolean {
        // Increase indent after colon (def, class, if, for, while, try, etc.),
        // and after an opening bracket
        return line.endsWith(":") || line.endsWithOpeningBracket()
    }

    private fun shouldIncreaseIndentJava(line: String): Boolean {
        // Increase indent after an opening bracket
        return line.endsWithOpeningBracket()
    }

    private fun shouldIncreaseIndentJavaScript(line: String): Boolean {
        // Increase indent after an opening bracket
        if (line.endsWithOpeningBracket()) return true

        // Increase indent after => in arrow functions
        if (line.endsWith("=>")) return true

        return false
    }

    private fun String.endsWithOpeningBracket(): Boolean = lastOrNull()?.let { it in CLOSING_BRACKETS } == true

    private companion object {
        /** Each opening bracket and the bracket that closes it. */
        val CLOSING_BRACKETS = mapOf('{' to '}', '(' to ')', '[' to ']')
    }

    private fun calculateNewIndent(baseIndent: String, shouldIncrease: Boolean): String {
        if (!shouldIncrease) {
            return baseIndent
        }

        return if (settings.useTabsState.value) {
            baseIndent + "\t"
        } else {
            baseIndent + " ".repeat(settings.tabSizeState.value)
        }
    }
}

/**
 * Factory to create language-specific auto-indent handlers
 */
@InternalNemoApi
object AutoIndentHandlerFactory {
    fun create(language: Language, settings: EditorSettings): AutoIndentHandler {
        return AutoIndentHandler(language, settings)
    }
}