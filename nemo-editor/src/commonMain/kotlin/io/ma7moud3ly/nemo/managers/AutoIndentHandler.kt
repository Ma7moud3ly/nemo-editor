package io.ma7moud3ly.nemo.managers

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.Language

/**
 * Handles automatic indentation when user presses Enter
 */
internal class AutoIndentHandler(
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

        val lineBreakEnd = edit.insertionEnd
        val newIndent = indentForLineBreakAt(newValue.text, edit.start)
        if (newIndent.isEmpty()) return null

        return TextFieldValue(
            text = newValue.text.substring(0, lineBreakEnd) +
                    newIndent +
                    newValue.text.substring(lineBreakEnd),
            selection = TextRange(lineBreakEnd + newIndent.length)
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
        // Increase indent after opening braces
        if (line.endsWith("{")) return true

        // Increase indent after = in expressions (but not in parameter lists)
        if (line.endsWith("=") && !line.contains("(")) return true

        // Increase indent after -> in when expressions
        if (line.endsWith("->")) return true

        // Increase indent after colon in labels
        if (line.matches(Regex(".*\\w+:\\s*"))) return true

        return false
    }

    private fun shouldIncreaseIndentPython(line: String): Boolean {
        // Increase indent after colon (def, class, if, for, while, try, etc.)
        return line.endsWith(":")
    }

    private fun shouldIncreaseIndentJava(line: String): Boolean {
        // Increase indent after opening braces
        return line.endsWith("{")
    }

    private fun shouldIncreaseIndentJavaScript(line: String): Boolean {
        // Increase indent after opening braces
        if (line.endsWith("{")) return true

        // Increase indent after => in arrow functions
        if (line.endsWith("=>")) return true

        return false
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
internal object AutoIndentHandlerFactory {
    fun create(language: Language, settings: EditorSettings): AutoIndentHandler {
        return AutoIndentHandler(language, settings)
    }
}