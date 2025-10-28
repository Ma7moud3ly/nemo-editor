package io.ma7moud3ly.nemo.managers

import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.NemoFile
import io.ma7moud3ly.nemo.model.SettingsData
import io.ma7moud3ly.nemo.model.SavedTabData
import io.ma7moud3ly.nemo.platform.Platform
import io.ma7moud3ly.nemo.platform.asPlatformFile
import io.ma7moud3ly.nemo.platform.exists
import io.ma7moud3ly.nemo.platform.getPlatform
import io.ma7moud3ly.nemo.platform.ioDispatcher
import io.ma7moud3ly.nemo.platform.isWasmJs
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import io.ma7moud3ly.nemo.model.EditorThemes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Manages persistence of editor state across app sessions
 *
 * Responsibilities:
 * - Save/restore open tabs
 * - Save/restore current directory
 * - Track recent files
 * - Persist editor settings
 * - Auto-save functionality
 *
 * @param tabsManager Reference to tabs manager for state access
 * @param filesManager Reference to files manager for directory state
 * @param editorSettings Reference to editor settings
 * @param settings Platform-specific settings implementation
 * @param ioDispatcher Coroutine dispatcher for IO operations
 * @param platform Current platform
 */
internal class PersistenceManager(
    private val tabsManager: TabsManager,
    private val filesManager: FilesManager,
    private val editorSettings: EditorSettings,
    private val settings: Settings = Settings(),
    private val ioDispatcher: CoroutineDispatcher = ioDispatcher(),
    private val platform: Platform = getPlatform()
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    companion object {
        private const val TAG = "PersistenceManager"

        // Keys for storage
        private const val KEY_OPEN_TABS = "open_tabs"
        private const val KEY_CURRENT_DIRECTORY = "current_directory"
        private const val KEY_ROOT_DIRECTORY = "root_directory"
        private const val KEY_RECENT_FILES = "recent_files"
        private const val KEY_RECENT_DIRECTORIES = "recent_directories"
        private const val KEY_THEME_NAME = "theme_name"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_TAB_SIZE = "tab_size"
        private const val KEY_USE_TABS = "use_tabs"
        private const val KEY_SHOW_LINE_NUMBERS = "show_line_numbers"
        private const val KEY_ENABLE_AUTO_INDENT = "enable_auto_indent"
        private const val KEY_ENABLE_AUTOCOMPLETE = "enable_autocomplete"
        private const val KEY_READ_ONLY = "read_only"
        private const val KEY_SIDEBAR_EXPANDED = "sidebar_expanded"
        private const val KEY_ANIMATED_LOGO = "animated_logo"

        private const val MAX_RECENT_FILES = 20
        private const val MAX_RECENT_DIRECTORIES = 10
    }

    // ==================== SAVE OPERATIONS ====================

    /**
     * Save all editor state (tabs, directory, settings)
     * Call this when app is pausing or closing
     */
    suspend fun saveAll() {
        withContext(ioDispatcher) {
            try {
                saveOpenTabs()
                saveCurrentDirectory()
                saveEditorSettings()
                println("$TAG: All state saved successfully")
            } catch (e: Exception) {
                println("$TAG: Error saving state: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Save currently open tabs and their state
     */
    fun saveOpenTabs() {
        try {
            val tabs = tabsManager.tabs
            val savedTabDataList = tabs.map { tab ->
                val file = tab.file
                SavedTabData(
                    id = tab.id,
                    filePath = file.path,
                    fileName = file.name,
                    tempContent = if (file.exists() && platform.isWasmJs.not()) ""
                    else tab.fileContent,
                    fileExtension = file.extension,
                    cursorPosition = tab.codeState.cursorPosition,
                    isDirty = file.exists().not() || platform.isWasmJs
                )
            }

            val tabsJson = json.encodeToString(savedTabDataList)
            settings[KEY_OPEN_TABS] = tabsJson
            println("$TAG: Saved ${tabs.size} tabs")
        } catch (e: Exception) {
            println("$TAG: Error saving tabs: ${e.message}")
        }
    }

    /**
     * Save current and root directory paths
     */
    fun saveCurrentDirectory() {
        try {
            filesManager.currentDirectoryPath?.let { currentDir ->
                settings[KEY_CURRENT_DIRECTORY] = currentDir
            }

            filesManager.rootDirectoryPath?.let { rootDir ->
                settings[KEY_ROOT_DIRECTORY] = rootDir
            }

            println("$TAG: Saved directory state")
        } catch (e: Exception) {
            println("$TAG: Error saving directory: ${e.message}")
        }
    }

    /**
     * Save editor settings (theme, font size, etc.)
     */
    fun saveEditorSettings() {
        try {
            val settingsData = SettingsData(
                themeName = editorSettings.themeState.value.name,
                fontSize = editorSettings.fontSizeState.value,
                tabSize = editorSettings.tabSizeState.value,
                useTabs = editorSettings.useTabsState.value,
                showLineNumbers = editorSettings.showLineNumbersState.value,
                enableAutoIndent = editorSettings.enableAutoIndentState.value,
                enableAutocomplete = editorSettings.enableAutocompleteState.value,
                readOnly = editorSettings.readOnlyState.value,
                sidebarExpanded = false, // This will be set from UiState
                animatedLogo = true // This will be set from UiState
            )

            // Save individual settings for easier access
            settings[KEY_THEME_NAME] = settingsData.themeName
            settings[KEY_FONT_SIZE] = settingsData.fontSize
            settings[KEY_TAB_SIZE] = settingsData.tabSize
            settings[KEY_USE_TABS] = settingsData.useTabs
            settings[KEY_SHOW_LINE_NUMBERS] = settingsData.showLineNumbers
            settings[KEY_ENABLE_AUTO_INDENT] = settingsData.enableAutoIndent
            settings[KEY_ENABLE_AUTOCOMPLETE] = settingsData.enableAutocomplete
            settings[KEY_READ_ONLY] = settingsData.readOnly

            println("$TAG: Saved editor settings")
        } catch (e: Exception) {
            println("$TAG: Error saving settings: ${e.message}")
        }
    }

    /**
     * Save UI state (sidebar, animated logo, etc.)
     */
    fun saveUiState(sidebarExpanded: Boolean, animatedLogo: Boolean) {
        settings[KEY_SIDEBAR_EXPANDED] = sidebarExpanded
        settings[KEY_ANIMATED_LOGO] = animatedLogo
    }

    /**
     * Add a file to recent files list
     */
    fun addRecentFile(nemoFile: NemoFile) {
        try {
            val filePath = nemoFile.path
            val recentFiles = getRecentFiles().toMutableList()
            recentFiles.remove(filePath) // Remove if exists
            recentFiles.add(0, filePath) // Add to front

            // Limit size
            val limitedFiles = recentFiles.take(MAX_RECENT_FILES)

            val json = json.encodeToString(limitedFiles)
            settings[KEY_RECENT_FILES] = json

            println("$TAG: Added recent file: $filePath")
        } catch (e: Exception) {
            println("$TAG: Error adding recent file: ${e.message}")
        }
    }

    /**
     * Add a directory to recent directories list
     */
    fun addRecentDirectory() {
        try {
            val directoryPath = filesManager.currentDirectoryPath ?: return
            val recentDirs = getRecentDirectories().toMutableList()
            recentDirs.remove(directoryPath)
            recentDirs.add(0, directoryPath)

            val limitedDirs = recentDirs.take(MAX_RECENT_DIRECTORIES)

            val json = json.encodeToString(limitedDirs)
            settings[KEY_RECENT_DIRECTORIES] = json

            println("$TAG: Added recent directory: $directoryPath")
        } catch (e: Exception) {
            println("$TAG: Error adding recent directory: ${e.message}")
        }
    }

    // ==================== RESTORE OPERATIONS ====================

    /**
     * Restore all editor state from storage
     * Call this when app is starting
     *
     * @return true if state was restored successfully
     */
    suspend fun restoreAll(): Boolean {
        return try {
            restoreEditorSettings()
            restoreCurrentDirectory()
            restoreOpenTabs()
            println("$TAG: All state restored successfully")
            true
        } catch (e: Exception) {
            println("$TAG: Error restoring state: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    /**
     * Restore open tabs from storage
     *
     * @return true if tabs were restored
     */
    suspend fun restoreOpenTabs(): Boolean {
        return try {
            val tabsJson = settings.getStringOrNull(KEY_OPEN_TABS) ?: return false
            val savedTabDataList = json.decodeFromString<List<SavedTabData>>(tabsJson)
            if (savedTabDataList.isEmpty()) return false

            // Recreate tabs
            savedTabDataList.reversed().forEach { tabData ->
                val platformFile = tabData.filePath.asPlatformFile()
                val file = NemoFile(
                    name = tabData.fileName,
                    path = tabData.filePath,
                    extension = tabData.fileExtension,
                    platformFile = platformFile
                )
                val content = if (file.exists()) filesManager.readFile(file).orEmpty()
                else tabData.tempContent
                tabsManager.createNewTab(file, content)
            }

            println("$TAG: Restored ${savedTabDataList.size} tabs")
            true
        } catch (e: Exception) {
            println("$TAG: Error restoring tabs: ${e.message}")
            false
        }
    }

    /**
     * Restore current directory from storage
     */
    suspend fun restoreCurrentDirectory(): Boolean {
        return try {
            val rootDir = settings.getStringOrNull(KEY_ROOT_DIRECTORY)
            val currentDir = settings.getStringOrNull(KEY_CURRENT_DIRECTORY)
            if (rootDir != null) {
                filesManager.setCurrentDirectory(
                    root = rootDir,
                    path = currentDir ?: rootDir
                )
                println("$TAG: Restored directory: $currentDir")
                return true
            }
            false
        } catch (e: Exception) {
            println("$TAG: Error restoring directory: ${e.message}")
            false
        }
    }

    /**
     * Restore editor settings from storage
     */
    fun restoreEditorSettings() {
        try {
            // Restore theme
            val themeName = settings.getStringOrNull(KEY_THEME_NAME)
            if (themeName != null) {
                val theme = EditorThemes.getThemeByName(themeName)
                editorSettings.themeState.value = theme
            }

            // Restore font size
            val fontSize = settings.getIntOrNull(KEY_FONT_SIZE)
            if (fontSize != null) {
                editorSettings.setFontSize(fontSize)
            }

            // Restore tab size
            val tabSize = settings.getIntOrNull(KEY_TAB_SIZE)
            if (tabSize != null) {
                editorSettings.setTabSize(tabSize)
            }

            // Restore boolean settings
            settings.getBooleanOrNull(KEY_USE_TABS)?.let {
                editorSettings.useTabsState.value = it
            }

            settings.getBooleanOrNull(KEY_SHOW_LINE_NUMBERS)?.let {
                editorSettings.showLineNumbersState.value = it
            }

            settings.getBooleanOrNull(KEY_ENABLE_AUTO_INDENT)?.let {
                editorSettings.enableAutoIndentState.value = it
            }

            settings.getBooleanOrNull(KEY_ENABLE_AUTOCOMPLETE)?.let {
                editorSettings.enableAutocompleteState.value = it
            }

            settings.getBooleanOrNull(KEY_READ_ONLY)?.let {
                editorSettings.readOnlyState.value = it
            }

            println("$TAG: Restored editor settings")
        } catch (e: Exception) {
            println("$TAG: Error restoring settings: ${e.message}")
        }
    }

    /**
     * Restore UI state
     */
    fun restoreUiState(): Pair<Boolean, Boolean> {
        val sidebarExpanded = settings.getBooleanOrNull(KEY_SIDEBAR_EXPANDED) ?: false
        val animatedLogo = settings.getBooleanOrNull(KEY_ANIMATED_LOGO) ?: true
        return Pair(sidebarExpanded, animatedLogo)
    }

    // ==================== GETTERS ====================

    /**
     * Get list of recent files
     */
    fun getRecentFiles(): List<String> {
        try {
            val files = settings.getStringOrNull(KEY_RECENT_FILES) ?: return emptyList()
            return json.decodeFromString<List<String>>(files)
        } catch (e: Exception) {
            println("$TAG: Error getting recent files: ${e.message}")
            return emptyList()
        }
    }

    /**
     * Get list of recent directories
     */
    fun getRecentDirectories(): List<String> {
        try {
            val txt = settings.getStringOrNull(KEY_RECENT_DIRECTORIES) ?: return emptyList()
            return json.decodeFromString<List<String>>(txt)
        } catch (e: Exception) {
            println("$TAG: Error getting recent directories: ${e.message}")
            return emptyList()
        }
    }

}