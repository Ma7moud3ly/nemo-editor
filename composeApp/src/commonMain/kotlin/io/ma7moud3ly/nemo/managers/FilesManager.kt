package io.ma7moud3ly.nemo.managers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ma7moud3ly.nemo.platform.platformExportFile
import io.ma7moud3ly.nemo.platform.getPlatformDirectoryPicker
import io.ma7moud3ly.nemo.managers.FilesManager.Companion.path
import io.ma7moud3ly.nemo.model.NemoFile
import io.ma7moud3ly.nemo.platform.platformCreateFile
import io.ma7moud3ly.nemo.platform.platformCreateFolder
import io.ma7moud3ly.nemo.platform.platformDelete
import io.ma7moud3ly.nemo.platform.platformListFiles
import io.ma7moud3ly.nemo.platform.platformPickFile
import io.ma7moud3ly.nemo.platform.platformRename
import io.ma7moud3ly.nemo.platform.platformSaveFile
import io.ma7moud3ly.nemo.platform.platformSaveFileAs
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readString
import io.github.vinceglb.filekit.size

/**
 * Complete cross-platform file manager for handling all file operations
 *
 * Features:
 * - File picker (open single/multiple files)
 * - Directory picker with recursive file listing
 * - File reading/writing with encoding support
 * - Directory navigation (up, into subdirectories)
 * - File system operations (create, delete, rename, copy, move)
 * - File watching for external changes
 * - Search in files
 * - File metadata (size, modified date, permissions)
 * - Recent directories tracking
 * - Favorites/bookmarks
 * - Loading states and error handling
 *
 * Uses FileKit for KMP compatibility
 */
internal class FilesManager(
    private val initialFiles: List<NemoFile> = emptyList(),
    private val initialRoot: String? = null
) {

    // ==================== STATE ====================

    // Current root directory path (base of file explorer)
    var rootDirectoryPath by mutableStateOf<String?>(initialRoot)
        private set

    // Current directory being browsed
    var currentDirectoryPath by mutableStateOf<String?>(null)
        private set

    // List of files in current directory
    val currentFiles = mutableStateListOf<NemoFile>().apply {
        addAll(initialFiles)
    }

    // Loading state
    var isLoading by mutableStateOf(false)
        private set

    // Error state
    var errorMessage by mutableStateOf<String?>(null)

    // Recent directories (last 10)
    var recentDirectories by mutableStateOf<List<String>>(emptyList())
        private set

    // ==================== FILE PICKER ====================

    /**
     * Pick a single file using FileKit
     ** @return Pair of file and content
     */
    suspend fun pickFile(): Pair<NemoFile?, String?> {
        return try {
            isLoading = true
            errorMessage = null
            val platformFile = FileKit.platformPickFile()
            val file = platformFile?.asNemoFile
            val content = platformFile?.readString()
            Pair(file, content)
        } catch (e: Exception) {
            println("pickFile-> ${e.message}")
            errorMessage = "Failed to pick file: ${e.message}"
            Pair(null, null)
        } finally {
            isLoading = false
        }
    }

    /**
     * Pick a directory using FileKit
     *
     * @param title Dialog title
     * @return true if directory was selected successfully
     */
    suspend fun pickDirectory(title: String = "Select Folder"): Boolean {
        return try {
            isLoading = true
            errorMessage = null

            val directory = FileKit.getPlatformDirectoryPicker(
                title = title,
                directory = null,
                dialogSettings = FileKitDialogSettings.createDefault()
            )

            if (directory != null) {
                val path = directory.toString().replace("\\", "/")
                rootDirectoryPath = path
                currentDirectoryPath = path
                addToRecentDirectories(path)
                loadDirectory(path)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            errorMessage = "Failed to pick directory: ${e.message}"
            false
        } finally {
            isLoading = false
        }
    }

    // ==================== FILE READING ====================

    /**
     * Read file by path
     *
     * @param path The file path
     * @return File content or null on error
     */
    suspend fun readFile(file: NemoFile): String? {
        return try {
            isLoading = true
            errorMessage = null
            file.platformFile?.readString()
        } catch (e: Exception) {
            errorMessage = "Failed to read file: ${e.message}"
            null
        } finally {
            isLoading = false
        }
    }


    // ==================== FILE WRITING ====================

    /**
     * Save file using FileKit save dialog
     *
     * @param file File to save
     * @param content File content
     * @return true if saved successfully
     */
    suspend fun saveFile(
        file: NemoFile,
        content: String
    ): Boolean {
        return try {
            isLoading = true
            errorMessage = null
            FileKit.platformSaveFile(file, content)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Failed to save file: ${e.message}"
            false
        } finally {
            isLoading = false
        }
    }

    suspend fun saveFileAs(
        file: NemoFile,
        content: String
    ): NemoFile? {
        try {
            isLoading = true
            errorMessage = null
            return FileKit.platformSaveFileAs(file, content)
        } catch (e: Exception) {
            errorMessage = "Failed to save file: ${e.message}"
            return null
        } finally {
            isLoading = false
        }
    }

    suspend fun exportFile(file: NemoFile, content: String) {
        FileKit.platformExportFile(
            file = file,
            content = content
        )
    }

    // ==================== DIRECTORY OPERATIONS ====================

    /**
     * Load directory contents
     *
     * @param path Directory path
     * @param sortBy Sort criteria
     */
    suspend fun loadDirectory(
        path: String,
        sortBy: FileSortCriteria = FileSortCriteria.FOLDER_FIRST
    ) {
        try {
            isLoading = true
            errorMessage = null
            val files = FileKit.platformListFiles(path).sortedWith(sortBy.comparator)
            currentFiles.clear()
            currentFiles.addAll(files)
        } catch (e: Exception) {
            errorMessage = "Failed to load directory: ${e.message}"
            currentFiles.clear()
        } finally {
            isLoading = false
        }
    }

    /**
     * Navigate up to parent directory
     */
    suspend fun navigateUp() {
        currentDirectoryPath?.let { current ->
            val parent = getParentPath(current)
            if (parent != null) {
                currentDirectoryPath = parent
                loadDirectory(parent)
            }
        }
    }

    /**
     * Navigate to a subdirectory
     *
     * @param path Directory path
     */
    suspend fun navigateToDirectory(file: NemoFile) {
        val path = file.path.replace("\\", "/")
        currentDirectoryPath = path
        addToRecentDirectories(path)
        loadDirectory(path)
    }

    suspend fun setCurrentDirectory(root: String, path: String) {
        rootDirectoryPath = root
        currentDirectoryPath = path
        addToRecentDirectories(path)
        loadDirectory(path)
    }

    /**
     * Navigate to root directory
     */
    suspend fun navigateToRoot() {
        rootDirectoryPath?.let { root ->
            currentDirectoryPath = root
            loadDirectory(root)
        }
    }

    /**
     * Refresh current directory
     */
    suspend fun refresh() {
        currentDirectoryPath?.let { loadDirectory(it) }
    }

    suspend fun createNewFolder(name: String) {
        val path = "$currentDirectoryPath/$name"
        FileKit.platformCreateFolder(path)
        refresh()
    }

    suspend fun createNewFile(name: String) {
        val path = "$currentDirectoryPath/$name"
        FileKit.platformCreateFile(path)
        refresh()
    }


    suspend fun deleteFile(file: NemoFile) {
        FileKit.platformDelete(file)
        refresh()
    }

    suspend fun renameFile(file: NemoFile, newName: String): NemoFile? {
        val file = FileKit.platformRename(file, newName)
        refresh()
        return file
    }


    // ==================== HELPER METHODS ====================

    /**
     * Get parent path from current path
     */
    fun getParentPath(path: String): String? {
        val separator = if (path.contains('/')) '/' else '\\'
        val lastIndex = path.lastIndexOf(separator)
        return if (lastIndex > 0) path.take(lastIndex) else null
    }


    /**
     * Clear error message
     */
    fun clearError() {
        errorMessage = null
    }

    // ==================== PERSISTENCE (TO BE IMPLEMENTED) ====================

    private fun addToRecentDirectories(path: String) {
        val updated = (listOf(path) + recentDirectories).distinct().take(10)
        recentDirectories = updated
        saveRecents()
    }

    private fun saveRecents() {
    }

    companion object {
        private const val TAG = "FilesManager"

        val PlatformFile?.path: String? get() = this?.toString()
        val PlatformFile?.asPath: String get() = this.toString().orEmpty()

        val PlatformFile.asNemoFile: NemoFile
            get() {
                return NemoFile(
                    name = this.name,
                    path = this.asPath,
                    extension = this.extension,
                    isDirectory = false,
                    platformFile = this,
                    size = this.size()
                )
            }

        internal val extensions = setOf(
            // Text files
            "txt",
            "md",
            "log",
            "csv",
            "tsv",
            "json",
            "xml",
            "yml",
            "yaml",
            "ini",
            "cfg",
            "conf",

            // Web
            "html",
            "htm",
            "css",
            "scss",
            "sass",
            "less",
            "js",
            "jsx",
            "ts",
            "tsx",
            "php",
            "asp",
            "aspx",
            "jsp",

            // Programming Languages
            "c",
            "cpp",
            "cc",
            "cxx",
            "h",
            "hpp",
            "hh",
            "java",
            "kt",
            "kts",
            "groovy",
            "gradle",
            "py",
            "pyw",
            "pyx",
            "pyd",
            "rb",
            "rbw",
            "go",
            "rs",
            "rlib",
            "swift",
            "m",
            "mm", // Objective-C
            "cs", // C#
            "fs",
            "fsi",
            "fsx",
            "fsscript", // F#
            "vb", // Visual Basic
            "pl",
            "pm",
            "t", // Perl
            "lua",
            "r",
            "sh",
            "bash",
            "zsh",
            "fish",
            "ps1",
            "psd1",
            "psm1", // PowerShell
            "bat",
            "cmd", // Batch
            "sql",
            "dart",
            "pas",
            "pp", // Pascal
            "d", // D
            "erl",
            "hrl", // Erlang
            "ex",
            "exs", // Elixir
            "hs",
            "lhs", // Haskell
            "clj",
            "cljs",
            "cljc",
            "edn", // Clojure
            "lisp",
            "lsp",
            "cl", // Lisp
            "scm",
            "ss", // Scheme
            "scala",
            "asm",
            "s", // Assembly
            "ada",
            "adb",
            "ads", // Ada
            "cob",
            "cbl", // COBOL
            "f",
            "for",
            "f90",
            "f95", // Fortran
            "jl", // Julia
            "nim", // Nim
            "v",
            "sv", // Verilog
            "vhdl",
            "vhd", // VHDL
            "zig",
            "cr", // Crystal
            "hx", // Haxe
            "ml",
            "mli", // OCaml
            "raku",
            "rakumod",
            "rakutest", // Raku
            "tcl", // TCL
            "vala", // Vala
            "sol", // Solidity
            "elm", // Elm
            "purs", // PureScript
            "res",
            "resi", // ReScript
            "gleam", // Gleam

            // Config & Data Serialization
            "toml",
            "json5",
            "hjson",
            "ron", // Rusty Object Notation
            "graphql",
            "gql",
            "proto", // Protocol Buffers

            // Markup & Templating
            "tex",
            "sty",
            "cls",
            "rtf",
            "sgml",
            "adoc",
            "asciidoc",
            "rst", // reStructuredText
            "hbs",
            "handlebars",
            "mustache",
            "ejs",
            "pug",
            "jade",
            "liquid",
            "svelte",

            // Other
            "dockerfile",
            "containerfile",
            "properties",
            "env",
            "gitignore",
            "gitattributes",
            "gitmodules",
            "editorconfig",
            "npmrc",
            "vim",
            "vimrc",
            "nvim",
            "zshrc",
            "bashrc",
            "bash_profile",
            "sublime-project",
            "sublime-workspace",
            "csproj",
            "fsproj",
            "vbproj",
            "sln",
            "vcxproj",
            "podspec",
            "gemfile"
        )

        internal fun NemoFile.getFileTypeLabel(): String {
            return when (this.extension.lowercase()) {
                "kt" -> "Kotlin Source File"
                "java" -> "Java Source File"
                "py" -> "Python Script"
                "js" -> "JavaScript File"
                "ts" -> "TypeScript File"
                "jsx" -> "React JSX File"
                "tsx" -> "React TSX File"
                "html" -> "HTML Document"
                "css" -> "CSS Stylesheet"
                "json" -> "JSON Data"
                "xml" -> "XML Document"
                "md" -> "Markdown Document"
                "txt" -> "Text Document"
                "gradle" -> "Gradle Build File"
                "properties" -> "Properties File"
                "yml", "yaml" -> "YAML Configuration"
                "sh" -> "Shell Script"
                "sql" -> "SQL Script"
                "cpp", "cc" -> "C++ Source File"
                "c" -> "C Source File"
                "h", "hpp" -> "Header File"
                "rs" -> "Rust Source File"
                "go" -> "Go Source File"
                "swift" -> "Swift Source File"
                "dart" -> "Dart File"
                else -> extension.uppercase().let { "$it File" }
            }
        }

    }
}


/**
 * File sort criteria
 */
internal enum class FileSortCriteria(val comparator: Comparator<NemoFile>) {
    NAME_ASC(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }),
    NAME_DESC(compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name }),
    SIZE_ASC(compareBy { it.size }),
    SIZE_DESC(compareByDescending { it.size }),
    DATE_ASC(compareBy { it.lastModified }),
    DATE_DESC(compareByDescending { it.lastModified }),
    TYPE_ASC(compareBy { it.extension }),
    TYPE_DESC(compareByDescending { it.extension }),
    FOLDER_FIRST(compareBy<NemoFile> { !it.isDirectory }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })
}