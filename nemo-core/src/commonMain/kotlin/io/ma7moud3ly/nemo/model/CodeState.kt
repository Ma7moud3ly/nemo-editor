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
import io.ma7moud3ly.nemo.InternalNemoApi
import io.ma7moud3ly.nemo.managers.UndoRedoManager
import io.ma7moud3ly.nemo.managers.AutoIndentHandler
import io.ma7moud3ly.nemo.managers.diffText

/**
 * State holder for the code editor with integrated undo/redo and text management
 *
 * This is the central state management class for the code editor.
 * It encapsulates all text operations, cursor management, and undo/redo functionality.
 *
 * @param initialCode Initial text content
 * @param language Language of the code
 * @param isDirty Whether the code starts with unsaved changes. When true,
 *   [contentChanged] stays true until the first [commitChanges].
 * @param initialCursorPosition Initial cursor position (default: end of text)
 */
@Stable
class CodeState(
    initialCode: String = "",
    val language: Language,
    isDirty: Boolean = false,
    initialCursorPosition: Int = initialCode.length
) {
    /**
     * Code editor text field value
     */
    @InternalNemoApi
    var value by mutableStateOf(
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
     * The text as of the last [commitChanges]. It is null while the code has
     * never been saved.
     */
    private var savedCode: String? by mutableStateOf(if (isDirty) null else initialCode)

    /**
     * True when the code differs from the text of the last [commitChanges].
     * Undoing or typing back to the saved text makes it false again.
     */
    val contentChanged: Boolean by derivedStateOf { code != savedCode }

    /**
     * Integrated undo/redo manager
     * Automatically manages action history for this editor state
     */
    private val undoRedoManager: UndoRedoManager = UndoRedoManager(state = this)

    /**
     * Set by [insertLineBreak] and cleared by the very next text change. If the
     * platform also delivers its own newline for that same key press, the flag
     * is still up and the duplicate is dropped. See [handleTextChange].
     */
    private var lineBreakJustHandled: Boolean = false

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
    @InternalNemoApi
    fun updateCodeValue(newValue: TextFieldValue) {
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

    /**
     * Marks the current text as saved, which makes [contentChanged] false.
     */
    fun commitChanges() {
        savedCode = code
    }

    /**
     * Insert completion item at current cursor position
     * Automatically replaces the partial word being typed
     *
     * @param insertText The text to insert
     */
    @InternalNemoApi
    fun insertCompletion(insertText: String) {
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
     * Break the line at the caret, carrying the indentation of the line being
     * left, and place the caret after that indentation. Any selected text is
     * replaced.
     *
     * This is driven by the Enter key rather than by the resulting text change.
     * Rewriting the text from inside a text-change callback leaves the platform
     * input method holding a stale copy of the editing state, and on Android it
     * reasserts its own caret afterwards - which is what dropped the caret to
     * column zero. Inserting the newline and its indentation together, before
     * the input method ever sees a newline, removes that exchange entirely.
     *
     * @param autoIndentHandler supplies the indentation; pass `null` to insert a
     *   bare line break.
     */
    @InternalNemoApi
    fun insertLineBreak(autoIndentHandler: AutoIndentHandler?) {
        val current = value
        val text = current.text
        val start = minOf(current.selection.start, current.selection.end)
            .coerceIn(0, text.length)
        val end = maxOf(current.selection.start, current.selection.end)
            .coerceIn(start, text.length)

        val inserted = "\n" + autoIndentHandler?.indentForLineBreakAt(text, start).orEmpty()

        if (end > start) {
            undoRedoManager.recordAction(
                FindReplaceAction.Delete(start, text.substring(start, end))
            )
        }
        undoRedoManager.recordAction(FindReplaceAction.Insert(start, inserted))

        val caret = start + inserted.length
        lineBreakJustHandled = true
        updateCodeValue(
            TextFieldValue(
                text = text.substring(0, start) + inserted + text.substring(end),
                selection = TextRange(caret)
            )
        )
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
    @InternalNemoApi
    fun handleTextChange(
        newValue: TextFieldValue,
        autoIndentHandler: AutoIndentHandler? = null
    ): Boolean {
        val oldValue = this.value
        val oldText = oldValue.text
        val newText = newValue.text

        // Record changes for undo/redo. The edit is derived from the two texts
        // rather than from the reported caret, which some input methods and the
        // web backend have not yet moved when the change arrives - and which
        // previously made these offsets run off the end of the string.
        val edit = diffText(oldText, newText)

        // Enter is normally handled by insertLineBreak, straight off the key
        // event, so the newline and its indentation land as one edit that no
        // input method can reorder. Should the platform deliver its own newline
        // for that same key press as well, drop it rather than break the line
        // twice.
        val justBrokeLine = lineBreakJustHandled
        lineBreakJustHandled = false
        if (justBrokeLine &&
            edit != null &&
            edit.removed.isEmpty() &&
            (edit.inserted == "\n" || edit.inserted == "\r\n")
        ) {
            return true
        }

        if (edit != null) {
            if (edit.removed.isNotEmpty()) {
                undoRedoManager.recordAction(
                    FindReplaceAction.Delete(edit.start, edit.removed)
                )
            }
            if (edit.inserted.isNotEmpty()) {
                undoRedoManager.recordAction(
                    FindReplaceAction.Insert(edit.start, edit.inserted)
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