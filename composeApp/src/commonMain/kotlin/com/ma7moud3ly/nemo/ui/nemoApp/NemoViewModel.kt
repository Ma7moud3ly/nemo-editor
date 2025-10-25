package com.ma7moud3ly.nemo.ui.nemoApp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma7moud3ly.nemo.managers.EditOperations
import com.ma7moud3ly.nemo.managers.FilesManager
import com.ma7moud3ly.nemo.managers.FormatManager
import com.ma7moud3ly.nemo.managers.LanguagesManager
import com.ma7moud3ly.nemo.managers.PersistenceManager
import com.ma7moud3ly.nemo.managers.ShortcutsManager
import com.ma7moud3ly.nemo.managers.TabsManager
import com.ma7moud3ly.nemo.model.AppRoutes
import com.ma7moud3ly.nemo.model.EditorAction
import com.ma7moud3ly.nemo.model.EditorSettings
import com.ma7moud3ly.nemo.themes.EditorThemes
import com.ma7moud3ly.nemo.model.NemoFile
import com.ma7moud3ly.nemo.model.UiState
import com.ma7moud3ly.nemo.platform.exists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Nemo Editor - Updated with  UI
 */
internal class NemoEditorViewModel : ViewModel() {

    companion object {
        private const val TAG = "NemoEditorViewModel"
        private val DEFAULT_THEME = EditorThemes.NEMO_DARK
    }

    val appVersion = "1.0"
    val buildNumber = "1"

    private var formatManager: FormatManager? = null
    private var editOperations: EditOperations? = null
    val filesManager = FilesManager()
    val tabsManager = TabsManager()
    val languagesManager = LanguagesManager()
    val shortcutsManager = ShortcutsManager(::handleEditorAction)

    val selectedFile = mutableStateOf<NemoFile?>(null)
    val uiState = UiState()
    val editorSettings = EditorSettings(theme = DEFAULT_THEME, fontSize = 14)

    val persistenceManager = PersistenceManager(
        tabsManager = tabsManager,
        filesManager = filesManager,
        editorSettings = editorSettings
    )

    private val _navEvents = MutableSharedFlow<AppRoutes>(replay = 1)
    val navEvents: Flow<AppRoutes> = _navEvents.asSharedFlow()

    // Pending actions for confirmation dialogs
    var pendingAction = mutableStateOf<(() -> Unit)?>(null)
        private set
    var pendingCloseTabId = mutableStateOf<String?>(null)
        private set
    var recentFiles = mutableStateOf<List<String>>(emptyList())
        private set

    init {
        // Observe active tab changes
        viewModelScope.launch {
            tabsManager.activeTabFlow.collect { _ ->
                val codeState = tabsManager.activeCodeState
                if (codeState != null) {
                    formatManager = FormatManager(codeState, editorSettings)
                    editOperations = EditOperations(codeState, editorSettings)
                } else {
                    formatManager = null
                    editOperations = null
                }
            }
        }
        restoreState()
    }

    // ==================== MAIN ACTION HANDLER ====================

    fun handleEditorAction(action: EditorAction) {
        viewModelScope.launch {
            when (action) {
                // ===== FILE OPERATIONS =====
                EditorAction.NewUntitledFile -> tabsManager.createEmptyTab()
                is EditorAction.NewFile -> tabsManager.createNewTab(action.file, "")
                EditorAction.PickFile -> handlePickFile()
                EditorAction.PickFolder -> handlePickFolder()
                is EditorAction.OpenFile -> handleOpenFile(action.file)
                EditorAction.Save -> handleSave()
                EditorAction.Export -> handleExportFile()
                EditorAction.SaveAs -> handleSaveAs()
                EditorAction.SaveAll -> handleSaveAll()
                EditorAction.ReloadFile -> handleReloadFile()
                EditorAction.Refresh -> filesManager.refresh()
                is EditorAction.OpenFolder -> filesManager.navigateToDirectory(action.file)
                EditorAction.NavigateUp -> filesManager.navigateUp()
                EditorAction.ShowUnsaved -> _navEvents.emit(AppRoutes.Dialog.UnSavedChanged)
                is EditorAction.RenameFile -> {
                    selectedFile.value = action.file
                    _navEvents.emit(AppRoutes.Dialog.File.Rename)
                }

                is EditorAction.DeleteFile -> {
                    selectedFile.value = action.file
                    _navEvents.emit(AppRoutes.Dialog.File.Delete)
                }

                is EditorAction.FileDetails -> {
                    selectedFile.value = action.file
                    _navEvents.emit(AppRoutes.Dialog.File.Details)
                }

                is EditorAction.CreateNewFile -> {
                    _navEvents.emit(AppRoutes.Dialog.File.Create)
                }

                is EditorAction.CreateNewFolder -> {
                    _navEvents.emit(AppRoutes.Dialog.Folder.Create)
                }

                // ===== TAB OPERATIONS =====
                EditorAction.CloseActiveTab -> handleCloseActiveTab()

                is EditorAction.CloseTab -> handleCloseTab(action.tabId)
                EditorAction.CloseAllTabs -> handleCloseAllTabs()
                is EditorAction.CloseOtherTabs -> handleCloseOtherTabs(action.tabId)
                is EditorAction.CloseTabsToRight -> handleCloseTabsToRight(action.tabId)
                EditorAction.CloseSavedTabs -> handleCloseSavedTabs()
                is EditorAction.SwitchTab -> tabsManager.switchTab(action.tabId)
                EditorAction.NextTab -> tabsManager.switchToNextTab()
                EditorAction.PreviousTab -> tabsManager.switchToPreviousTab()
                is EditorAction.SwitchTabByIndex -> handleSwitchTabByIndex(action.index)

                // ===== EDIT OPERATIONS =====
                EditorAction.Undo -> tabsManager.activeCodeState?.undo()
                EditorAction.Redo -> tabsManager.activeCodeState?.redo()
                EditorAction.DuplicateLine -> editOperations?.duplicateLine()
                EditorAction.DeleteLine -> editOperations?.deleteLine()
                EditorAction.ToggleComment -> editOperations?.toggleComment()
                EditorAction.Indent -> editOperations?.indent()
                EditorAction.Unindent -> editOperations?.unindent()
                EditorAction.Format -> formatManager?.format()
                EditorAction.SelectAll -> handleSelectAll()
                EditorAction.ToggleFind,
                EditorAction.ToggleReplace -> uiState.toggleFindAndReplace()

                // ===== VIEW OPERATIONS =====
                EditorAction.ZoomIn -> editorSettings.zoomIn()
                EditorAction.ZoomOut -> editorSettings.zoomOut()
                EditorAction.ZoomReset -> handleZoomReset()
                EditorAction.ToggleTheme -> _navEvents.emit(AppRoutes.Dialog.Themes)
                EditorAction.CycleTheme -> handleCycleTheme()
                is EditorAction.ShowSideBar -> uiState.showSidebar(action.show)
                EditorAction.ToggleSideBar -> uiState.toggleSidebar()
                EditorAction.ToggleLineNumbers -> editorSettings.toggleLinesNumber()
                EditorAction.ToggleReadOnly -> editorSettings.toggleReadOnly()
                EditorAction.ToggleLogo -> uiState.toggleAnimatedLogo()
                EditorAction.OpenSettings -> _navEvents.emit(AppRoutes.Dialog.Settings)
                EditorAction.ShowShortcuts -> _navEvents.emit(AppRoutes.Dialog.Shortcuts)
                EditorAction.ShowAbout -> _navEvents.emit(AppRoutes.Dialog.About)

                else -> handleUnimplementedAction(action)
            }
        }
    }

    // ==================== FILE OPERATIONS ====================


    private fun handlePickFile() {
        viewModelScope.launch {
            val (file, content) = filesManager.pickFile()
            if (content != null && file != null) {
                tabsManager.createNewTab(file, content)
                persistenceManager.addRecentFile(file)
            }
        }
    }

    private fun handlePickFolder() {
        viewModelScope.launch {
            val success = filesManager.pickDirectory()
            if (success) {
                persistenceManager.addRecentDirectory()
                uiState.showSidebar(true)
            }
        }
    }

    private fun handleOpenFile(file: NemoFile) {
        viewModelScope.launch {
            try {
                val content = filesManager.readFile(file).orEmpty()
                tabsManager.createNewTab(file, content)
                persistenceManager.addRecentFile(file)
            } catch (e: Exception) {
                filesManager.errorMessage = "Failed to open file: ${e.message}"
            }
        }
    }

    private fun handleSave() {
        val tab = tabsManager.activeTab ?: return
        println("Saving file: ${tab.file.path}")
        viewModelScope.launch {
            if (tab.file.exists()) {
                val success = filesManager.saveFile(tab.file, tab.fileContent)
                if (success) {
                    tabsManager.commitChanges(tab.id)
                    persistenceManager.addRecentFile(tab.file)
                }
            } else {
                handleSaveAs()
            }
        }
    }


    private fun handleSaveAs() {
        val tab = tabsManager.activeTab ?: return
        viewModelScope.launch {
            val newFile = filesManager.saveFileAs(tab.file, tab.fileContent)
            if (newFile != null) {
                tabsManager.commitChanges(tab.id)
                tabsManager.updateActiveTab(newFile)
            }
        }
    }

    private fun handleExportFile() {
        val tab = tabsManager.activeTab ?: return
        viewModelScope.launch {
            filesManager.exportFile(
                file = tab.file,
                content = tab.fileContent
            )
        }
    }

    private fun handleSaveAll() {
        viewModelScope.launch {
            tabsManager.getDirtyTabs().forEach { tab ->
                val codeState = tabsManager.getCodeState(tab.id)
                if (codeState != null && tab.file.exists()) {
                    val success = filesManager.saveFile(
                        file = tab.file,
                        content = tab.fileContent
                    )
                    if (success) {
                        tabsManager.commitChanges(tab.id)
                    }
                }
            }
        }
    }

    private fun handleReloadFile() {
        viewModelScope.launch {
            val tab = tabsManager.activeTab ?: return@launch
            if (tab.file.exists()) {
                val content = filesManager.readFile(tab.file)
                if (content != null) {
                    tabsManager.activeCodeState?.updateText(content)
                    tabsManager.commitChanges(tab.id)
                }
            }
        }
    }

    fun deleteFile(file: NemoFile) {
        viewModelScope.launch {
            filesManager.deleteFile(file)
            tabsManager.forceCloseTab(file)
        }
    }

    fun createNewFile(name: String) {
        viewModelScope.launch {
            filesManager.createNewFile(name)
        }
    }

    fun createNewFolder(name: String) {
        viewModelScope.launch {
            filesManager.createNewFolder(name)
        }
    }

    fun renameFile(file: NemoFile, newName: String) {
        viewModelScope.launch {
            val newFile = filesManager.renameFile(file, newName) ?: return@launch
            if (newFile.exists()) {
                tabsManager.updateTab(file, newFile)
            }
        }
    }

    // ==================== TAB OPERATIONS ====================

    private suspend fun handleCloseActiveTab() {
        val tab = tabsManager.activeTab ?: return
        handleCloseTab(tab.id)
    }

    private suspend fun handleCloseTab(tabId: String) {
        val closed = tabsManager.closeTab(tabId)
        if (!closed) {
            pendingCloseTabId.value = tabId
            _navEvents.emit(AppRoutes.Dialog.UnSavedChanged)
            pendingAction.value = {
                tabsManager.forceCloseTab(tabId)
            }
        }
    }

    private suspend fun handleCloseAllTabs() {
        val dirtyTabs = tabsManager.closeAllTabs()
        if (dirtyTabs.isNotEmpty()) {
            _navEvents.emit(AppRoutes.Dialog.UnSavedChanged)
            pendingAction.value = {
                tabsManager.forceCloseAllTabs()
            }
        }
    }

    private suspend fun handleCloseOtherTabs(tabId: String) {
        val dirtyTabs = tabsManager.closeOtherTabs(tabId)
        if (dirtyTabs.isNotEmpty()) {
            _navEvents.emit(AppRoutes.Dialog.UnSavedChanged)
            pendingAction.value = {
                tabsManager.forceCloseAllTabs()
            }
        }
    }

    private suspend fun handleCloseTabsToRight(tabId: String) {
        val dirtyTabs = tabsManager.closeTabsToRight(tabId)
        if (dirtyTabs.isNotEmpty()) {
            _navEvents.emit(AppRoutes.Dialog.UnSavedChanged)
            pendingAction.value = { /* Force close */ }
        }
    }

    private fun handleCloseSavedTabs() {
        tabsManager.tabs.filter { !it.contentChanged() }.forEach { tab ->
            tabsManager.forceCloseTab(tab.id)
        }
    }

    private fun handleSwitchTabByIndex(index: Int) {
        if (index in tabsManager.tabs.indices) {
            tabsManager.switchTab(tabsManager.tabs[index].id)
        }
    }

    // ==================== EDIT OPERATIONS ====================

    private fun handleSelectAll() {
        val state = tabsManager.activeCodeState ?: return
        state.setSelection(0, state.code.length)
    }

    // ==================== STATE PERSISTENCE ====================

    /**
     * Restore editor state from storage
     * Called on app start
     */
    private fun restoreState() {
        viewModelScope.launch {
            try {
                // Restore settings first
                persistenceManager.restoreEditorSettings()

                // Restore UI state
                val (sidebarExpanded, animatedLogo) = persistenceManager.restoreUiState()
                uiState.sidebarExpanded.value = sidebarExpanded
                uiState.showAnimatedLogo.value = animatedLogo

                // Restore tabs and directory
                persistenceManager.restoreAll()

                // Load recent files
                recentFiles.value = persistenceManager.getRecentFiles()

                println("State restored successfully")
            } catch (e: Exception) {
                println("Error restoring state: ${e.message}")
            }
        }
    }

    /**
     * Save editor state to storage
     * Called periodically or on app pause
     */
    fun saveState() {
        viewModelScope.launch {
            try {
                // Save UI state
                persistenceManager.saveUiState(
                    sidebarExpanded = uiState.sidebarExpanded.value,
                    animatedLogo = uiState.showAnimatedLogo.value
                )
                // Save all tabs
                tabsManager.saveAllTabs()
                // Save all state
                persistenceManager.saveAll()

                println("State saved successfully")
            } catch (e: Exception) {
                println("Error saving state: ${e.message}")
            }
        }
    }

    // ==================== VIEW OPERATIONS ====================


    private fun handleZoomReset() {
        editorSettings.fontSizeState.value = 14
    }

    private fun handleCycleTheme() {
        val themes = EditorThemes.ALL_THEMES
        val currentIndex = themes.indexOf(editorSettings.themeState.value)
        val nextIndex = (currentIndex + 1) % themes.size
        editorSettings.themeState.value = themes[nextIndex]
    }

    fun resetPendingAction() {
        pendingAction.value = null
    }

    private fun handleUnimplementedAction(action: EditorAction) {
        println("Unimplemented action: ${action::class.simpleName}")
    }

}
