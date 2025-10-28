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

            // Update the state with formatted text
            // Place cursor at the end of the document
            state.updateText(formatted, formatted.length)
        }
    }
}