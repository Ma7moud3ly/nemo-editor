package io.ma7moud3ly.nemo.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.ma7moud3ly.nemo.managers.UndoRedoManager
import io.ma7moud3ly.nemo.managers.AutoIndentHandler

/**
 * State holder for the code editor with integrated undo/redo and text management
 *
 * This is the central state management class for the code editor.
 * It encapsulates all text operations, cursor management, and undo/redo functionality.
 *
 * @param initialCode Initial text content
 * @param language Language of the code
 * @param isDirty Whether the code has unsaved changes
 * @param initialCursorPosition Initial cursor position (default: end of text)
 */
@Stable
class CodeState(
    initialCode: String = "",
    val language: Language,
    isDirty: Boolean = false,
    initialCursorPosition: Int = initialCode.length
) {
    var contentChanged by mutableStateOf(isDirty)
        internal set

    /**
     * Code editor text field value
     */
    internal var value by mutableStateOf(
        TextFieldValue(
            text = initialCode,
            selection = TextRange(initialCursorPosition)
        )
    )


    /**
     * Current text content
     */
    val code: String get() = value.text

    /**
     * Integrated undo/redo manager
     * Automatically manages action history for this editor state
     */
    private val undoRedoManager: UndoRedoManager = UndoRedoManager(
        state = this,
        onContentChanges = { contentChanged = it }
    )

    /**
     * Current cursor position (zero-based)
     */
    val cursorPosition: Int
        get() = value.selection.start

    /**
     * Current text selection range
     */
    val selection: TextRange
        get() = value.selection

    /**
     * Total number of lines in the document
     */
    val totalLines: Int by derivedStateOf {
        code.count { it == '\n' } + 1
    }

    /**
     * Current line number (1-based)
     */
    val currentLine: Int by derivedStateOf {
        code.substring(0, cursorPosition.coerceAtMost(code.length))
            .count { it == '\n' } + 1
    }

    /**
     * Update the text content with a new cursor position
     *
     * @param newText The new text content
     * @param newCursorPosition The new cursor position (default: end of text)
     */
    fun updateText(newText: String, newCursorPosition: Int = newText.length) {
        value = TextFieldValue(
            text = newText,
            selection = TextRange(newCursorPosition)
        )
    }

    /**
     * Update the text field value directly
     * Internal use only - for framework integration
     */
    internal fun updateCodeValue(newValue: TextFieldValue) {
        value = newValue
    }

    /**
     * Set text selection range
     *
     * @param start Start position of selection
     * @param end End position of selection
     */
    fun setSelection(start: Int, end: Int) {
        val safeStart = start.coerceIn(0, code.length)
        val safeEnd = end.coerceIn(safeStart, code.length)
        value = value.copy(
            selection = TextRange(safeStart, safeEnd)
        )
    }

    /**
     * Perform undo operation
     * Reverts the last recorded action
     */
    fun undo() {
        undoRedoManager.undo()
    }

    /**
     * Perform redo operation
     * Re-applies the last undone action
     */
    fun redo() {
        undoRedoManager.redo()
    }

    /**
     * Check if undo is available
     *
     * @return true if there are actions to undo
     */
    fun canUndo(): Boolean = undoRedoManager.canUndo()

    /**
     * Check if redo is available
     *
     * @return true if there are actions to redo
     */
    fun canRedo(): Boolean = undoRedoManager.canRedo()

    /**
     * Clear undo/redo history
     * Useful when loading a new document or resetting the editor
     */
    fun clearHistory() {
        undoRedoManager.clear()
    }

    fun commitChanges() {
        contentChanged = false
    }

    /**
     * Insert completion item at current cursor position
     * Automatically replaces the partial word being typed
     *
     * @param insertText The text to insert
     */
    internal fun insertCompletion(insertText: String) {
        val cursorPos = this.cursorPosition
        val separators = " \n\t(){}[].,;:\"'<>="

        // Find start of current word
        val wordStart = this.code.lastIndexOfAny(
            separators.toCharArray(),
            (cursorPos - 1).coerceAtLeast(0)
        ) + 1

        val oldText = this.code
        val newText = oldText.take(wordStart) +
                insertText +
                oldText.substring(cursorPos)

        val newCursorPos = wordStart + insertText.length

        // Record for undo/redo
        val replacedText = oldText.substring(wordStart, cursorPos)
        undoRedoManager.recordAction(
            FindReplaceAction.Replace(
                start = wordStart,
                end = cursorPos,
                oldText = replacedText,
                newText = insertText
            )
        )

        updateText(newText, newCursorPos)
    }

    /**
     * Handle text field value change with undo/redo recording and auto-indent
     *
     * This method encapsulates all the logic for handling user text input:
     * - Records insert/delete actions for undo/redo
     * - Applies auto-indentation when Enter is pressed
     * - Updates the editor state
     *
     * @param newValue The new text field value from user input
     * @param autoIndentHandler Optional auto-indent handler for Enter key processing
     * @return true if the change was handled successfully
     */
    internal fun handleTextChange(
        newValue: TextFieldValue,
        autoIndentHandler: AutoIndentHandler? = null
    ): Boolean {
        val oldValue = this.value
        val oldText = oldValue.text
        val newText = newValue.text

        // Record changes for undo/redo
        if (oldText != newText) {
            if (newText.length > oldText.length) {
                // Text was inserted
                val insertPos = newValue.selection.start - (newText.length - oldText.length)
                val insertedText = newText.substring(insertPos, newValue.selection.start)
                undoRedoManager.recordAction(
                    FindReplaceAction.Insert(insertPos, insertedText)
                )
            } else if (newText.length < oldText.length) {
                // Text was deleted
                val deletePos = newValue.selection.start
                val deletedText = oldText.substring(
                    deletePos,
                    deletePos + (oldText.length - newText.length)
                )
                undoRedoManager.recordAction(
                    FindReplaceAction.Delete(deletePos, deletedText)
                )
            }
        }

        // Handle auto-indent if handler is provided
        if (autoIndentHandler != null) {
            val autoIndentedValue = autoIndentHandler.handleTextChange(
                oldValue,
                newValue
            )
            if (autoIndentedValue != null) {
                updateCodeValue(autoIndentedValue)
                return true
            }
        }

        // Update with new value
        updateCodeValue(newValue)
        return true
    }

    /**
     * Record an action for undo/redo
     *
     * @param action The action to record
     */
    fun recordAction(action: FindReplaceAction) {
        undoRedoManager.recordAction(action)
    }
}

/**
 * Remember a code editor state across recompositions
 *
 * @param code Initial text content
 * @param initialCursorPosition Initial cursor position (default: end of text)
 * @return A remembered CodeEditorState instance
 */
@Composable
fun rememberCodeState(
    code: String = "",
    language: Language,
    initialCursorPosition: Int = code.length
): CodeState {
    return remember {
        CodeState(
            initialCode = code,
            language = language,
            initialCursorPosition = initialCursorPosition
        )
    }
}