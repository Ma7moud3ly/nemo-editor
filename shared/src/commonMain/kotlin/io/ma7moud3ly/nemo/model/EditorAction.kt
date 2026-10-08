package io.ma7moud3ly.nemo.model

/**
 * Complete sealed interface for all editor actions
 * Centralized event system for the entire editor
 */
sealed interface EditorAction {

    // ==================== FILE OPERATIONS ====================

    /**
     * Create a new untitled file
     * Shortcut: Ctrl+N
     */
    data object NewUntitledFile : EditorAction

    /**
     * Create a new file with a given name+language
     */
    data class NewFile(val file: NemoFile) : EditorAction

    /**
     * Open file picker dialog
     * Shortcut: Ctrl+O
     */
    data object PickFile : EditorAction

    /**
     * Open folder picker dialog
     * Shortcut: Ctrl+Shift+O
     */
    data object PickFolder : EditorAction

    /**
     * Open a specific file from file explorer
     * @param file The file to open
     */
    data class OpenFile(val file: NemoFile) : EditorAction

    /**
     * Open a specific folder from file explorer
     * @param file The folder to open
     */
    data class OpenFolder(val file: NemoFile) : EditorAction

    /**
     * Save the active file
     * Shortcut: Ctrl+S
     */
    data object Save : EditorAction

    /**
     * Save the active file with a new name
     * Shortcut: Ctrl+Shift+S
     */
    data object SaveAs : EditorAction

    /**
     * Export file
     */
    data object Export : EditorAction

    /**
     * Save all open files
     * Shortcut: Ctrl+K S
     */
    data object SaveAll : EditorAction

    /**
     * Show unsaved changes dialog
     */
    data object ShowUnsaved : EditorAction

    /**
     * Reload file from disk (discard changes)
     */
    data object ReloadFile : EditorAction

    /**
     * Reload all open files from disk
     */
    data object NavigateUp : EditorAction

    /**
     * Reload all open files
     */
    data object Refresh : EditorAction

    /**
     * Create a new file in the current directory
     */
    data object CreateNewFile : EditorAction


    /**
     * Create a new folder in the current directory
     */
    data object CreateNewFolder : EditorAction

    /**
     * Rename a file or folder
     * @param file The file/folder to rename
     */
    data class RenameFile(val file: NemoFile) : EditorAction

    /**
     * Delete a file or folder
     * @param file The file/folder to delete
     */
    data class DeleteFile(val file: NemoFile) : EditorAction


    /**
     * Show file details dialog
     * @param file The file to show details for
     */
    data class FileDetails(val file: NemoFile) : EditorAction

    // ==================== TAB OPERATIONS ====================

    /**
     * Close the active tab
     * Shortcut: Ctrl+W
     */
    data object CloseActiveTab : EditorAction

    /**
     * Close a specific tab by ID
     * @param tabId The tab ID to close
     */
    data class CloseTab(val tabId: String) : EditorAction

    /**
     * Close all open tabs
     * Shortcut: Ctrl+Shift+W
     */
    data object CloseAllTabs : EditorAction

    /**
     * Close all tabs except the specified one
     * @param tabId The tab to keep open
     */
    data class CloseOtherTabs(val tabId: String) : EditorAction

    /**
     * Close all tabs to the right of specified tab
     * @param tabId The reference tab
     */
    data class CloseTabsToRight(val tabId: String) : EditorAction

    /**
     * Close all saved tabs (keep only dirty tabs)
     */
    data object CloseSavedTabs : EditorAction

    /**
     * Switch to a specific tab
     * @param tabId The tab to activate
     */
    data class SwitchTab(val tabId: String) : EditorAction

    /**
     * Switch to the next tab
     * Shortcut: Ctrl+Tab
     */
    data object NextTab : EditorAction

    /**
     * Switch to the previous tab
     * Shortcut: Ctrl+Shift+Tab
     */
    data object PreviousTab : EditorAction

    /**
     * Switch to tab by index (1-9)
     * Shortcut: Ctrl+1, Ctrl+2, etc.
     * @param index The tab index (0-based)
     */
    data class SwitchTabByIndex(val index: Int) : EditorAction


    // ==================== EDIT OPERATIONS ====================

    /**
     * Undo last action
     * Shortcut: Ctrl+Z
     */
    data object Undo : EditorAction

    /**
     * Redo last undone action
     * Shortcut: Ctrl+Y or Ctrl+Shift+Z
     */
    data object Redo : EditorAction


    /**
     * Select all text
     * Shortcut: Ctrl+A
     */
    data object SelectAll : EditorAction

    /**
     * Duplicate current line
     * Shortcut: Ctrl+D
     */
    data object DuplicateLine : EditorAction

    /**
     * Delete current line
     * Shortcut: Ctrl+L or Ctrl+Shift+K
     */
    data object DeleteLine : EditorAction

    /**
     * Toggle line comment
     * Shortcut: Ctrl+/
     */
    data object ToggleComment : EditorAction


    /**
     * Indent selection
     * Shortcut: Ctrl+]
     */
    data object Indent : EditorAction

    /**
     * Unindent selection
     * Shortcut: Ctrl+[
     */
    data object Unindent : EditorAction

    /**
     * Format document or selection
     * Shortcut: Ctrl+Shift+F
     */
    data object Format : EditorAction


    // ==================== SEARCH & REPLACE ====================

    /**
     * Open find dialog
     * Shortcut: Ctrl+F
     */
    data object ToggleFind : EditorAction

    /**
     * Open find and replace dialog
     * Shortcut: Ctrl+H
     */
    data object ToggleReplace : EditorAction

    // ==================== VIEW OPERATIONS ====================

    /**
     * Zoom in (increase font size)
     * Shortcut: Ctrl+Plus or Ctrl+=
     */
    data object ZoomIn : EditorAction

    /**
     * Zoom out (decrease font size)
     * Shortcut: Ctrl+Minus
     */
    data object ZoomOut : EditorAction

    /**
     * Reset zoom to default
     * Shortcut: Ctrl+0
     */
    data object ZoomReset : EditorAction

    /**
     * Toggle theme selector dialog
     */
    data object ToggleTheme : EditorAction

    /**
     * Cycle through themes
     */
    data object CycleTheme : EditorAction

    /**
     * Toggle between light and dark theme
     */
    data object ToggleLightDark : EditorAction

    /**
     * Toggle side bar visibility
     * Shortcut: Ctrl+B
     */
    data object ToggleSideBar : EditorAction

    /**
     * Toggle status bar visibility
     * Shortcut: Ctrl+Shift+B
     */
    data class ShowSideBar(val show: Boolean) : EditorAction

    /**
     * Toggle line numbers
     */
    data object ToggleLineNumbers : EditorAction


    /**
     * Toggle read-only mode
     */
    data object ToggleReadOnly : EditorAction

    /**
     * Toggle animated logo
     */
    data object ToggleLogo : EditorAction

    /**
     * Open settings dialog
     */
    data object OpenSettings : EditorAction

    /**
     * Open keyboard shortcuts dialog
     */
    data object ShowShortcuts : EditorAction

    // ==================== HELP & INFO ====================

    /**
     * Show documentation
     */
    data object ShowHelp : EditorAction

    /**
     * Show about dialog
     */
    data object ShowAbout : EditorAction

}