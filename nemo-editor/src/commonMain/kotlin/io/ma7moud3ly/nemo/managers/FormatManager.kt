package io.ma7moud3ly.nemo.managers

import io.ma7moud3ly.nemo.model.FindReplaceAction
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.syntax.formatter.FormatterFactory

/**
 * Manages code formatting with automatic state updates and undo/redo integration
 *
 * @param state The editor state to format
 * @param settings The editor settings for formatting
 */
class FormatManager(
    private val state: CodeState,
    private val settings: EditorSettings
) {
    private val formatter = FormatterFactory.getFormatter(state.language)

    /**
     * Format the current code
     *
     * This method:
     * 1. Gets the current text from the editor state
     * 2. Formats it using the language-specific formatter
     * 3. Records the format action in undo history
     * 4. Updates the editor state with the formatted text
     *
     * If the text doesn't change after formatting, no action is taken.
     */
    fun format() {
        if (formatter == null) return
        val oldText = state.code
        val tabSize = settings.tabSizeState.value
        val useTabs = settings.useTabsState.value
        val formatted = try {
            formatter.format(oldText, tabSize, useTabs)
        } catch (e: Exception) {
            e.printStackTrace()
            oldText
        }
        // Only update if formatting actually changed the text
        if (oldText != formatted) {
            // Record the action for undo/redo
            state.recordAction(
                FindReplaceAction.Replace(
                    start = 0,
                    end = oldText.length,
                    oldText = oldText,
                    newText = formatted
                )
            )

            // Update the state with formatted text, keeping the caret on the
            // same code so the editor does not scroll away
            val cursor = matchingOffset(oldText, formatted, state.cursorPosition)
            state.updateText(formatted, cursor)
        }
    }
}

/**
 * The offset in [formatted] that matches [offset] in [original], where
 * formatting changed mostly whitespace: the one after the same number of
 * non-whitespace characters. When [offset] sits in whitespace after them, it
 * keeps the same number of line breaks, and inside an indentation it goes to
 * the end of the new indentation.
 */
internal fun matchingOffset(original: String, formatted: String, offset: Int): Int {
    val caret = offset.coerceIn(0, original.length)

    var code = 0
    for (i in 0 until caret) if (!original[i].isWhitespace()) code++

    // Line breaks between the last code character and the caret
    var lineBreaks = 0
    var i = caret - 1
    while (i >= 0 && original[i].isWhitespace()) {
        if (original[i] == '\n') lineBreaks++
        i--
    }
    // The caret is in the indentation of a line below the code
    val inIndentation = lineBreaks > 0 && original[caret - 1] != '\n'

    var result = 0
    while (result < formatted.length && code > 0) {
        if (!formatted[result].isWhitespace()) code--
        result++
    }
    while (lineBreaks > 0 && result < formatted.length && formatted[result].isWhitespace()) {
        if (formatted[result] == '\n') lineBreaks--
        result++
    }
    if (inIndentation) {
        while (result < formatted.length && (formatted[result] == ' ' || formatted[result] == '\t')) {
            result++
        }
    }
    return result
}