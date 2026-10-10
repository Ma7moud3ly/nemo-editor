package io.ma7moud3ly.nemo.tabs

import androidx.compose.runtime.mutableStateListOf
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Holds the open tabs, in the order they were opened, and the active one.
 *
 * It only tracks tabs. Reading and writing what a tab edits is left to the app.
 *
 * @param initialTabs tabs that are open from the start
 * @param activeTabId id of the tab that starts active
 */
class TabsManager(
    initialTabs: List<NemoTab> = emptyList(),
    activeTabId: String? = null
) {
    private val _tabs = mutableStateListOf<NemoTab>().apply { addAll(initialTabs) }

    /** The open tabs, in order. */
    val tabs: List<NemoTab> get() = _tabs.toList()

    private val _activeTabFlow = MutableStateFlow(_tabs.find { it.id == activeTabId })
    val activeTabFlow: Flow<NemoTab?> = _activeTabFlow.asStateFlow()

    val activeTab: NemoTab? get() = _activeTabFlow.value

    /** The code state of the active tab. */
    val activeCodeState: CodeState? get() = _activeTabFlow.value?.codeState

    /** The code state of the tab with [tabId]. */
    fun getCodeState(tabId: String): CodeState? = getTab(tabId)?.codeState

    /** The tab with [tabId]. */
    fun getTab(tabId: String): NemoTab? = _tabs.find { it.id == tabId }

    /** The open tab whose document is at [path]. */
    fun findTab(path: String): NemoTab? = _tabs.find { it.path == path }

    /**
     * Opens [document] in a tab and makes it active. When a document with the
     * same path is already open, its tab becomes active and no tab is added.
     *
     * @param isDirty whether the new tab starts with unsaved changes
     */
    fun openTab(
        document: TabFile,
        content: String,
        language: Language,
        isDirty: Boolean = false
    ): NemoTab {
        val existingTab = findTab(document.path)
        if (existingTab != null) {
            _activeTabFlow.value = existingTab
            return existingTab
        }
        return addTab(document, content, language, isDirty)
    }

    /**
     * Adds a tab for [document] and makes it active, even when a document
     * with the same path is already open.
     */
    @OptIn(ExperimentalUuidApi::class)
    fun addTab(
        document: TabFile,
        content: String = "",
        language: Language = Language.BLANK,
        isDirty: Boolean = false
    ): NemoTab {
        val tab = NemoTab(
            id = Uuid.random().toString(),
            tabFile = document,
            codeState = CodeState(
                initialCode = content,
                language = language,
                isDirty = isDirty
            )
        )
        _tabs.add(tab)
        _activeTabFlow.value = tab
        return tab
    }

    /**
     * Points the tab with [tabId] at [document], for example after a rename
     * or a "save as". The tab keeps its place and its text.
     *
     * When [language] differs from the tab's language, the tab gets a new code
     * state with the same text, which clears its undo history.
     */
    fun updateTab(tabId: String, document: TabFile, language: Language? = null) {
        val index = _tabs.indexOfFirst { it.id == tabId }
        if (index == -1) return
        val tab = _tabs[index]
        val codeState = if (language == null || language == tab.codeState.language) {
            tab.codeState
        } else {
            CodeState(initialCode = tab.content, language = language)
        }
        val newTab = tab.copy(tabFile = document, codeState = codeState)
        _tabs[index] = newTab
        if (_activeTabFlow.value?.id == tabId) _activeTabFlow.value = newTab
    }

    /**
     * Closes the tab with [tabId] unless it has unsaved changes.
     *
     * @return false when the tab is dirty and was left open
     */
    fun closeTab(tabId: String): Boolean {
        if (getTab(tabId)?.isDirty == true) return false
        return forceCloseTab(tabId)
    }

    /** Closes the tab with [tabId], even with unsaved changes. */
    fun forceCloseTab(tabId: String): Boolean {
        _tabs.removeAll { it.id == tabId }
        // Switch to another tab if the closed tab was active
        if (_activeTabFlow.value?.id == tabId) {
            _activeTabFlow.value = _tabs.lastOrNull()
        }
        return true
    }

    /**
     * Closes every tab when none has unsaved changes.
     *
     * @return the dirty tabs; when it is not empty nothing was closed
     */
    fun closeAllTabs(): List<NemoTab> {
        val dirtyTabs = getDirtyTabs()
        if (dirtyTabs.isEmpty()) forceCloseAllTabs()
        return dirtyTabs
    }

    /** Closes every tab, even with unsaved changes. */
    fun forceCloseAllTabs() {
        _tabs.clear()
        _activeTabFlow.value = null
    }

    /**
     * Closes every tab except [tabId] when none of them has unsaved changes.
     *
     * @return the dirty tabs; when it is not empty nothing was closed
     */
    fun closeOtherTabs(tabId: String): List<NemoTab> {
        val dirtyTabs = _tabs.filter { it.id != tabId && it.isDirty }
        if (dirtyTabs.isEmpty()) {
            val tab = getTab(tabId) ?: return emptyList()
            _tabs.removeAll { it.id != tabId }
            _activeTabFlow.value = tab
        }
        return dirtyTabs
    }

    /**
     * Closes the tabs after [tabId] when none of them has unsaved changes.
     *
     * @return the dirty tabs; when it is not empty nothing was closed
     */
    fun closeTabsToRight(tabId: String): List<NemoTab> {
        val index = _tabs.indexOfFirst { it.id == tabId }
        if (index == -1 || index == _tabs.lastIndex) return emptyList()

        val tabsToClose = _tabs.subList(index + 1, _tabs.size).toList()
        val dirtyTabs = tabsToClose.filter { it.isDirty }
        if (dirtyTabs.isEmpty()) {
            tabsToClose.forEach { forceCloseTab(it.id) }
        }
        return dirtyTabs
    }

    /** Makes the tab with [tabId] active. */
    fun switchTab(tabId: String) {
        getTab(tabId)?.let { _activeTabFlow.value = it }
    }

    /** Makes the next tab active, wrapping to the first one. */
    fun switchToNextTab() {
        if (_tabs.isEmpty()) return
        val currentIndex = _tabs.indexOfFirst { it.id == _activeTabFlow.value?.id }
        _activeTabFlow.value = _tabs[(currentIndex + 1) % _tabs.size]
    }

    /** Makes the previous tab active, wrapping to the last one. */
    fun switchToPreviousTab() {
        if (_tabs.isEmpty()) return
        val currentIndex = _tabs.indexOfFirst { it.id == _activeTabFlow.value?.id }
        _activeTabFlow.value = if (currentIndex > 0) _tabs[currentIndex - 1] else _tabs.last()
    }

    /** Marks the tab with [tabId] as saved. */
    fun commitChanges(tabId: String) {
        getTab(tabId)?.codeState?.commitChanges()
    }

    /** The tabs with unsaved changes. */
    fun getDirtyTabs(): List<NemoTab> = _tabs.filter { it.isDirty }
}
