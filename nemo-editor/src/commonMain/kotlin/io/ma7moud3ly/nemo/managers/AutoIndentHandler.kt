package io.ma7moud3ly.nemo.managers

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.Language

/**
 * Handles automatic indentation when user presses Enter
 */
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
        // Check if Enter was pressed
        if (!isEnterPressed(oldValue, newValue)) {
            return null
        }

        val cursorPosition = newValue.selection.start
        if (cursorPosition == 0) return null

        // Get previous line
        val textBeforeCursor = newValue.text.substring(0, cursorPosition - 1)
        val previousLine = getPreviousLine(textBeforeCursor)

        // Calculate indent
        val baseIndent = getLineIndent(previousLine)
        val shouldIncreaseIndent = shouldIncreaseIndent(previousLine)
        val newIndent = calculateNewIndent(baseIndent, shouldIncreaseIndent)

        // Apply indent
        if (newIndent.isNotEmpty()) {
            val newText = newValue.text.substring(0, cursorPosition) +
                    newIndent +
                    newValue.text.substring(cursorPosition)

            return TextFieldValue(
                text = newText,
                selection = TextRange(cursorPosition + newIndent.length)
            )
        }

        return null
    }

    private fun isEnterPressed(oldValue: TextFieldValue, newValue: TextFieldValue): Boolean {
        return newValue.text.length > oldValue.text.length &&
                newValue.selection.start > 0 &&
                newValue.text[newValue.selection.start - 1] == '\n'
    }

    private fun getPreviousLine(textBeforeCursor: String): String {
        val lastNewlineIndex = textBeforeCursor.lastIndexOf('\n')
        return if (lastNewlineIndex == -1) {
            textBeforeCursor
        } else {
            textBeforeCursor.substring(lastNewlineIndex + 1)
        }
    }

    private fun getLineIndent(line: String): String {
        return line.takeWhile { it == ' ' || it == '\t' }
    }

    private fun shouldIncreaseIndent(line: String): Boolean {
        val trimmedLine = line.trimEnd()

        return when (language) {
            Language.KOTLIN -> shouldIncreaseIndentKotlin(trimmedLine)
            Language.PYTHON -> shouldIncreaseIndentPython(trimmedLine)
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
object AutoIndentHandlerFactory {
    fun create(language: Language, settings: EditorSettings): AutoIndentHandler {
        return AutoIndentHandler(language, settings)
    }
}