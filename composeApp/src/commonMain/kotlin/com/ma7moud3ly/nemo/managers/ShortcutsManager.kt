package com.ma7moud3ly.nemo.managers

import androidx.compose.ui.input.key.*
import com.ma7moud3ly.nemo.model.EditorAction

/**
 * Updated keyboard shortcuts handler with tab navigation and file explorer toggle
 *
 * New shortcuts:
 * - Ctrl+Tab: Next tab
 * - Ctrl+Shift+Tab: Previous tab
 * - Ctrl+W: Close active tab
 * - Ctrl+Shift+W: Close all tabs
 * - Ctrl+B: Toggle file explorer
 */
class ShortcutsManager(
    val action: (EditorAction) -> Unit
) {
    fun handleKeyEvent(event: KeyEvent): Boolean {
        if (event.type != KeyEventType.KeyDown) return false

        val ctrl = event.isCtrlPressed
        val shift = event.isShiftPressed
        val alt = event.isAltPressed
        return when {
            // Undo/Redo
            ctrl && shift && event.key == Key.Z -> {
                action(EditorAction.Redo); true
            }

            ctrl && event.key == Key.Y -> {
                action(EditorAction.Redo); true
            }

            ctrl && !shift && event.key == Key.Z -> {
                action(EditorAction.Undo); true
            }

            // File operations
            ctrl && event.key == Key.S -> {
                action(EditorAction.Save); true
            }

            ctrl && shift && event.key == Key.S -> {
                action(EditorAction.SaveAs); true
            }

            ctrl && event.key == Key.O -> {
                action(EditorAction.PickFile); true
            }

            ctrl && event.key == Key.N -> {
                action(EditorAction.NewUntitledFile); true
            }

            ctrl && shift && event.key == Key.O -> {
                action(EditorAction.PickFolder); true
            }

            // Tab operations
            ctrl && event.key == Key.W -> {
                action(EditorAction.CloseActiveTab); true
            }

            ctrl && shift && event.key == Key.W -> {
                action(EditorAction.CloseAllTabs); true
            }

            ctrl && event.key == Key.Tab -> {
                if (shift) {
                    action(EditorAction.PreviousTab)
                } else {
                    action(EditorAction.NextTab)
                }
                true
            }

            // View toggles
            ctrl && event.key == Key.B -> {
                action(EditorAction.ToggleSideBar); true
            }

            // Search
            ctrl && !shift && event.key == Key.F -> {
                action(EditorAction.ToggleFind); true
            }

            ctrl && event.key == Key.H -> {
                action(EditorAction.ToggleReplace); true
            }

            // Format
            ctrl && shift && event.key == Key.F -> {
                action(EditorAction.Format); true
            }

            // Zoom
            ctrl && event.key == Key.NumPadAdd -> {
                action(EditorAction.ZoomIn); true
            }

            ctrl && event.key == Key.Equals -> {
                action(EditorAction.ZoomIn); true
            }

            ctrl && event.key == Key.NumPadSubtract -> {
                action(EditorAction.ZoomOut); true
            }

            ctrl && event.key == Key.Minus -> {
                action(EditorAction.ZoomOut); true
            }

            // Line operations
            ctrl && event.key == Key.Slash -> {
                action(EditorAction.ToggleComment); true
            }

            ctrl && event.key == Key.D -> {
                action(EditorAction.DuplicateLine); true
            }

            ctrl && event.key == Key.L -> {
                action(EditorAction.DeleteLine); true
            }

            // Indentation
            ctrl && event.key == Key.RightBracket -> {
                action(EditorAction.Indent); true
            }

            ctrl && event.key == Key.LeftBracket -> {
                action(EditorAction.Unindent); true
            }

            else -> false
        }
    }

    companion object {
        private const val TAG = "ShortcutsManager"

    }
}