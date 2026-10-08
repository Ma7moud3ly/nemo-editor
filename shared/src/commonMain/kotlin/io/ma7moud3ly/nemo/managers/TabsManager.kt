package io.ma7moud3ly.nemo.managers

import androidx.compose.runtime.mutableStateMapOf
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorTab
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.model.NemoFile
import io.ma7moud3ly.nemo.model.asLanguage
import io.ma7moud3ly.nemo.platform.exists
import io.ma7moud3ly.nemo.platform.platformSaveFile
import io.github.vinceglb.filekit.FileKit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Improved manager for handling multiple editor tabs with proper state management
 */
class TabsManager(
    initialTabs: List<EditorTab> = emptyList(),
    activeTabId: String? = null
) {
    private val _tabs = mutableStateMapOf<String, EditorTab>().apply {
        putAll(initialTabs.associateBy { it.id })
    }
    val tabs: List<EditorTab> get() = _tabs.values.toList()

    private val _activeTabFlow = MutableStateFlow<EditorTab?>(_tabs[activeTabId])
    val activeTabFlow: Flow<EditorTab?> = _activeTabFlow.asStateFlow()

    val activeTab: EditorTab? get() = _activeTabFlow.value

    /**
     * Get the code state for a specific tab
     */
    fun getCodeState(tabId: String): CodeState? {
        return _tabs[tabId]?.codeState
    }

    /**
     * Get the code state for the active tab
     */
    val activeCodeState: CodeState? get() = _activeTabFlow.value?.codeState


    /**
     * Create a new untitled tab with specified language
     */
    @OptIn(ExperimentalUuidApi::class)
    fun createNewTab(file: NemoFile, content: String) {
        // Check if file is already open
        val existingTab = getTabByFile(file)
        if (existingTab != null) {
            _activeTabFlow.value = existingTab
            return
        }
        val tabId = Uuid.random().toString()
        val tab = EditorTab(
            id = tabId,
            file = file,
            CodeState(
                initialCode = content,
                isDirty = file.exists().not(),
                language = file.extension.asLanguage()
            )
        )

        _tabs[tabId] = tab
        _activeTabFlow.value = tab
    }

    /**
     * Update the file of a tab
     */
    fun updateTab(oldFile: NemoFile, newFile: NemoFile) {
        val tab = getTabByFile(oldFile) ?: return
        val newTab = tab.copy(file = newFile)
        _tabs[tab.id] = newTab
        _activeTabFlow.value = newTab
    }

    /**
     * Update the active tab with a new file
     */
    fun updateActiveTab(file: NemoFile) {
        val activeTab = _activeTabFlow.value ?: return
        val content = activeTab.fileContent
        val newTab = EditorTab(
            id = activeTab.id,
            file = file,
            codeState = CodeState(
                initialCode = content,
                language = file.extension.asLanguage()
            )
        )
        _activeTabFlow.value = newTab
        _tabs[newTab.id] = newTab
    }


    @OptIn(ExperimentalUuidApi::class)
    fun createEmptyTab(): String {
        val tabId = Uuid.random().toString()
        val tab = EditorTab(
            id = tabId,
            file = NemoFile(
                name = "untitled",
                path = "",
                extension = "",
            ),
            CodeState(
                initialCode = "",
                language = Language.BLANK
            ),
            isDirty = false
        )

        _tabs[tabId] = tab
        _activeTabFlow.value = tab
        return tabId
    }

    /**
     * Save all tabs to disk
     */
    suspend fun saveAllTabs() {
        println("Saving all tabs")
        _tabs.values.forEach { tab ->
            if (tab.file.exists() && tab.contentChanged()) {
                FileKit.platformSaveFile(tab.file, tab.fileContent)
            }
        }
    }

    /**
     * Close a tab
     * Returns false if tab has unsaved changes (requires confirmation)
     */
    fun closeTab(tabId: String): Boolean {
        val tab = _tabs[tabId]
        if (tab?.contentChanged() == true) {
            // Return false to indicate unsaved changes
            return false
        }

        return forceCloseTab(tabId)
    }

    /**
     * Force close a tab (even if dirty)
     */
    fun forceCloseTab(tabId: String): Boolean {
        _tabs.remove(tabId)
        // Switch to another tab if the closed tab was active
        if (_activeTabFlow.value?.id == tabId) {
            _activeTabFlow.value = when {
                _tabs.isEmpty() -> null
                else -> _tabs.values.lastOrNull()
            }
        }

        return true
    }

    /**
     * Force close a tab by file
     */
    fun forceCloseTab(file: NemoFile): Boolean {
        val tab = getTabByFile(file) ?: return false
        return forceCloseTab(tab.id)
    }


    /**
     * Close all tabs
     * Returns list of tabs with unsaved changes
     */
    fun closeAllTabs(): List<EditorTab> {
        val dirtyTabs = getDirtyTabs()
        if (dirtyTabs.isEmpty()) {
            forceCloseAllTabs()
        }
        return dirtyTabs
    }

    /**
     * Force close all tabs
     */
    fun forceCloseAllTabs() {
        _tabs.clear()
        _activeTabFlow.value = null
    }

    /**
     * Close all tabs except the specified one
     */
    fun closeOtherTabs(tabId: String): List<EditorTab> {
        val dirtyTabs = _tabs.values.filter { it.id != tabId && it.contentChanged() }
        if (dirtyTabs.isEmpty()) {
            val tab = _tabs[tabId] ?: return emptyList()
            _tabs.clear()
            _tabs[tab.id] = tab
            _activeTabFlow.value = tab
        }
        return dirtyTabs
    }

    /**
     * Close tabs to the right
     */
    fun closeTabsToRight(tabId: String): List<EditorTab> {
        val index = _tabs.keys.indexOfFirst { it == tabId }
        if (index == -1 || index == _tabs.size - 1) return emptyList()

        val tabsToClose = _tabs.values.toList().subList(index + 1, _tabs.size)
        val dirtyTabs = tabsToClose.filter { it.contentChanged() }

        if (dirtyTabs.isEmpty()) {
            tabsToClose.forEach { forceCloseTab(it.id) }
        }
        return dirtyTabs
    }

    /**
     * Switch to a specific tab
     */
    fun switchTab(tabId: String) {
        _tabs[tabId]?.let {
            _activeTabFlow.value = it
        }
    }

    /**
     * Switch to next tab
     */
    fun switchToNextTab() {
        val currentIndex = _tabs.keys.indexOfFirst { it == _activeTabFlow.value?.id }
        if (currentIndex != -1 && currentIndex < _tabs.size - 1) {
            _activeTabFlow.value = _tabs.values.toList()[currentIndex + 1]
        } else if (_tabs.isNotEmpty()) {
            _activeTabFlow.value = _tabs.values.firstOrNull()
        }
    }

    /**
     * Switch to previous tab
     */
    fun switchToPreviousTab() {
        val currentIndex = _tabs.keys.indexOfFirst { it == _activeTabFlow.value?.id }
        if (currentIndex > 0) {
            _activeTabFlow.value = _tabs.values.toList()[currentIndex - 1]
        } else if (_tabs.isNotEmpty()) {
            _activeTabFlow.value = _tabs.values.lastOrNull()
        }
    }

    /**
     * Commit changes to a tab
     */
    fun commitChanges(tabId: String) {
        println("commitChanges: $tabId")
        _tabs[tabId]?.codeState?.commitChanges()
    }

    /**
     * Get all dirty tabs
     */
    fun getDirtyTabs(): List<EditorTab> {
        return _tabs.values.filter { it.contentChanged() }
    }

    /**
     * Get a tab by file
     */
    fun getTabByFile(file: NemoFile): EditorTab? {
        return _tabs.values.find { it.file.path == file.path }
    }

}