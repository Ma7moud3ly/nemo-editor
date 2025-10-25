package com.ma7moud3ly.nemo.managers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.ma7moud3ly.nemo.model.FindReplaceAction
import com.ma7moud3ly.nemo.model.CodeState

/**
 * Manages undo/redo operations for the code editor
 *
 * This manager maintains two stacks:
 * - undoStack: Actions that can be undone
 * - redoStack: Actions that can be redone
 *
 * When an action is recorded, it's added to the undo stack and the redo stack is cleared.
 * When undo is called, the action is moved from undo stack to redo stack.
 * When redo is called, the action is moved from redo stack to undo stack.
 *
 * @param state The editor state that this manager will modify
 * @param onContentChanges Optional callback to trigger UI updates when content changes
 */
class UndoRedoManager(
    private val state: CodeState,
    private val onContentChanges: (Boolean) -> Unit = {}
) {
    private val undoStack = mutableListOf<FindReplaceAction>()
    private val redoStack = mutableListOf<FindReplaceAction>()
    private val maxStackSize = 100

    // Observable state for UI to react to changes
    private var undoCountState by mutableIntStateOf(0)
    private var redoCountState by mutableIntStateOf(0)

    /**
     * Record a new action in the undo stack
     *
     * This method:
     * 1. Adds the action to the undo stack
     * 2. Limits the stack size to maxStackSize
     * 3. Clears the redo stack (can't redo after a new action)
     *
     * @param action The action to record
     */
    fun recordAction(action: FindReplaceAction) {
        undoStack.add(action)

        // Limit stack size
        if (undoStack.size > maxStackSize) {
            undoStack.removeAt(0)
        }

        // Clear redo stack when new action is recorded
        redoStack.clear()

        // Update observable state
        updateCounts()
    }

    /**
     * Undo the last action
     *
     * This method:
     * 1. Pops the last action from the undo stack
     * 2. Applies the inverse of the action to the state
     * 3. Adds the action to the redo stack
     *
     * Does nothing if undo stack is empty.
     */
    fun undo() {
        if (undoStack.isEmpty()) return

        val action = undoStack.removeLast()
        redoStack.add(action)

        // Apply the inverse action
        applyAction(action, isUndo = true)

        // Update observable state
        updateCounts()
    }

    /**
     * Redo the last undone action
     *
     * This method:
     * 1. Pops the last action from the redo stack
     * 2. Applies the action to the state
     * 3. Adds the action back to the undo stack
     *
     * Does nothing if redo stack is empty.
     */
    fun redo() {
        if (redoStack.isEmpty()) return

        val action = redoStack.removeLast()
        undoStack.add(action)

        // Apply the action
        applyAction(action, isUndo = false)

        // Update observable state
        updateCounts()
    }

    /**
     * Apply an action to the editor state
     *
     * @param action The action to apply
     * @param isUndo If true, applies the inverse of the action (undo), otherwise applies normally (redo)
     */
    private fun applyAction(action: FindReplaceAction, isUndo: Boolean) {
        val currentText = state.code

        when (action) {
            is FindReplaceAction.Insert -> {
                if (isUndo) {
                    // Remove the inserted text
                    val newText = currentText.removeRange(
                        action.position,
                        (action.position + action.text.length).coerceAtMost(currentText.length)
                    )
                    state.updateText(newText, action.position.coerceAtMost(newText.length))
                } else {
                    // Insert the text
                    val newText = currentText.take(action.position) +
                            action.text +
                            currentText.substring(action.position)
                    state.updateText(
                        newText,
                        (action.position + action.text.length).coerceAtMost(newText.length)
                    )
                }
            }

            is FindReplaceAction.Delete -> {
                if (isUndo) {
                    // Re-insert the deleted text
                    val newText = currentText.take(action.position) +
                            action.text +
                            currentText.substring(action.position)
                    state.updateText(
                        newText,
                        (action.position + action.text.length).coerceAtMost(newText.length)
                    )
                } else {
                    // Delete the text
                    val newText = currentText.removeRange(
                        action.position,
                        (action.position + action.text.length).coerceAtMost(currentText.length)
                    )
                    state.updateText(newText, action.position.coerceAtMost(newText.length))
                }
            }

            is FindReplaceAction.Replace -> {
                val safeStart = action.start.coerceAtMost(currentText.length)
                val safeEnd = action.end.coerceAtMost(currentText.length)
                val replaceText = if (isUndo) action.oldText else action.newText

                val newText = if (safeStart <= safeEnd) {
                    currentText.replaceRange(safeStart, safeEnd, replaceText)
                } else {
                    replaceText
                }

                state.updateText(
                    newText,
                    (safeStart + replaceText.length).coerceAtMost(newText.length)
                )
            }
        }
    }

    /**
     * Update observable counts to trigger recomposition
     */
    private fun updateCounts() {
        undoCountState = undoStack.size
        redoCountState = redoStack.size
        onContentChanges(undoStack.size > 0)
    }

    /**
     * Check if undo is available
     *
     * @return true if there are actions in the undo stack
     */
    fun canUndo(): Boolean {
        return undoStack.isNotEmpty()
    }

    /**
     * Check if redo is available
     *
     * @return true if there are actions in the redo stack
     */
    fun canRedo(): Boolean {
        return redoStack.isNotEmpty()
    }

    /**
     * Clear both undo and redo stacks
     *
     * This is useful when loading a new document or resetting the editor
     */
    fun clear() {
        undoStack.clear()
        redoStack.clear()
        updateCounts()
    }
}