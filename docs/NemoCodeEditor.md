# 📘 NemoCodeEditor API Documentation

Complete reference for integrating NemoCodeEditor into your Kotlin Multiplatform projects.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Compose-1.7+-4285F4?logo=jetpackcompose&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web-blue)
![License](https://img.shields.io/badge/License-MIT-green)

---

## 📦 Installation
```kotlin
commonMain.dependencies {
    implementation("io.github.ma7moud3ly:nemo-editor:x.x.x")

    // Optional modules
    implementation("io.github.ma7moud3ly:nemo-search:x.x.x")
    implementation("io.github.ma7moud3ly:nemo-tabs:x.x.x")
}
```

Replace `x.x.x` with the latest version on [Maven Central](https://central.sonatype.com/artifact/io.github.ma7moud3ly/nemo-editor). All modules share the same version.

| Module | Contents |
|--------|----------|
| `nemo-editor` | `NemoCodeEditor`, with `CodeState`, `EditorSettings` and the themes |
| `nemo-search` | `FindReplaceBar`, `FindReplaceDialog` and `FindAndReplaceManager` |
| `nemo-tabs` | `TabsManager`, `NemoTab`, `TabFile` and `NemoTabs` |

---

## 🎯 Core API

Module: `nemo-editor`. It includes `CodeState`, `EditorSettings` and the themes.
```kotlin
implementation("io.github.ma7moud3ly:nemo-editor:x.x.x")
```

### NemoCodeEditor

Main editor component.
```kotlin
@Composable
fun NemoCodeEditor(
    state: CodeState,
    modifier: Modifier = Modifier,
    settings: EditorSettings = EditorSettings()
)
```

**Parameters:**
- `state` - Editor content state
- `settings` - Editor configuration
- `modifier` - Compose modifier

---

## 📝 CodeState

Manages editor content, cursor, and history.

### Constructor
```kotlin
CodeState(
    initialCode: String = "",
    language: Language,
    isDirty: Boolean = false,
    initialCursorPosition: Int = initialCode.length
)
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `code` | `String` | Current code content |
| `language` | `Language` | Programming language |
| `cursorPosition` | `Int` | Caret offset, zero-based |
| `selection` | `TextRange` | Current selection |
| `totalLines` | `Int` | Number of lines |
| `currentLine` | `Int` | Line the caret is on, one-based |
| `currentColumn` | `Int` | Column the caret is on, one-based |
| `contentChanged` | `Boolean` | Whether the code differs from the last `commitChanges()` |

### Methods
```kotlin
fun undo()
fun redo()
fun canUndo(): Boolean
fun canRedo(): Boolean
fun clearHistory()
fun updateText(newText: String, newCursorPosition: Int = newText.length)
fun setSelection(start: Int, end: Int)
// Change tracking
fun commitChanges()  // Mark as saved
```

### Example: Basic Editor
```kotlin
@Composable
fun MyEditor() {
    val codeState = rememberCodeState(
        code = "fun main() {\n    println(\"Hello\")\n}",
        language = Language.KOTLIN
    )
    NemoCodeEditor(state = codeState)
}
```
<div align="center">
  <img src="https://raw.githubusercontent.com/Ma7moud3ly/nemo-editor/refs/heads/main/images/docs/exmple1.png" alt="Basic Kotlin Editor" width="400"/>
  <p><em>Simple Kotlin editor with default theme and settings</em></p>
</div>

### Example: Python Editor
```kotlin
@Composable
fun PythonEditor() {
    val codeState = rememberCodeState(
        code = """
                def fibonacci(n):
                    if n <= 1:
                        return n
                    return fibonacci(n-1) + fibonacci(n-2)
                
                for i in range(10):
                    print(fibonacci(i))
            """.trimIndent(),
        language = Language.PYTHON
    )


    val editorSettings = remember {
        EditorSettings(
            theme = EditorThemes.NEMO_DARK,
            tabSize = 4,
        )
    }

    NemoCodeEditor(
        state = codeState,
        settings = settings,
        modifier = Modifier.fillMaxSize()
    )
}

```
<div align="center">
  <img src="https://raw.githubusercontent.com/Ma7moud3ly/nemo-editor/refs/heads/main/images/docs/exmple2.png" alt="Basic Kotlin Editor" width="400"/>
  <p><em>Python editor</em></p>
</div>

---

## ⚙️ EditorSettings

Controls editor appearance and behavior.

### Constructor
```kotlin
EditorSettings(
    theme: EditorTheme = EditorThemes.VS_CODE_DARK,
    tabSize: Int = 4,
    useTabs: Boolean = false,
    showLineNumbers: Boolean = true,
    showIndentGuides: Boolean = true,
    fontSize: Int = 14,
    fontFamily: FontFamily = FontFamily.Monospace,
    enableAutoIndent: Boolean = true,
    enableAutocomplete: Boolean = true,
    readOnly: Boolean = false
)
```

### Properties

All properties are exposed as `MutableState` for reactive updates:

| Property | Type | Description |
|----------|------|-------------|
| `themeState` | `MutableState<EditorTheme>` | Color theme |
| `fontSizeState` | `MutableState<Int>` | Font size (8-32) |
| `fontFamilyState` | `MutableState<FontFamily>` | Font of the code, line numbers and autocomplete |
| `tabSizeState` | `MutableState<Int>` | Tab width (2-8) |
| `useTabsState` | `MutableState<Boolean>` | Use tabs vs spaces |
| `showLineNumbersState` | `MutableState<Boolean>` | Show line numbers |
| `showIndentGuidesState` | `MutableState<Boolean>` | Show indentation guides |
| `enableAutocompleteState` | `MutableState<Boolean>` | Enable autocomplete |
| `enableAutoIndentState` | `MutableState<Boolean>` | Enable auto-indent |
| `readOnlyState` | `MutableState<Boolean>` | Read-only mode |

### Methods
```kotlin
fun zoomIn(value: Int = 2)
fun zoomOut(value: Int = 2)
fun setFontSize(value: Int)
fun setTabSize(value: Int)
fun increaseTabSize(value: Int = 1)
fun decreaseTabSize(value: Int = 1)
fun toggleLinesNumber()
fun toggleIndentGuides()
fun toggleReadOnly()
fun getIndentString(): String
```

### Example: Custom Font
```kotlin
// A font from your app's Compose resources
val jetBrainsMono = FontFamily(Font(Res.font.jetbrains_mono_regular))

val settings = remember(jetBrainsMono) {
    EditorSettings(fontFamily = jetBrainsMono)
}

// Or change it while the editor is on screen
settings.fontFamilyState.value = FontFamily.Monospace
```

Use a monospaced font, so columns and indentation guides line up.

---

## 🔍 Find and Replace

Module: `nemo-search`. Both components search a `CodeState`, select the current
match in the editor, and replace one match or all of them.
```kotlin
implementation("io.github.ma7moud3ly:nemo-search:x.x.x")
```

### FindReplaceBar

A bar meant to sit above or below the editor. It uses a single row on wide
screens and a stacked layout when it is narrower than 600dp.
```kotlin
@Composable
fun FindReplaceBar(
    state: CodeState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showReplace: Boolean = false,
    manager: FindAndReplaceManager = rememberFindAndReplaceManager(state)
)
```

### FindReplaceDialog

The same search in a floating dialog, with match case, whole word and regex as
chips. Enter in the find field moves to the next match, and Enter in the
replace field replaces the current one.
```kotlin
@Composable
fun FindReplaceDialog(
    state: CodeState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showReplace: Boolean = true,
    manager: FindAndReplaceManager = rememberFindAndReplaceManager(state)
)
```

**Parameters:**
- `state` - The editor state to search in
- `onDismiss` - Called when the user closes the bar or dialog
- `showReplace` - Whether the replace controls are shown
- `manager` - Holds the search text, options and matches

### Example: Editor with a Search Bar
```kotlin
@Composable
fun SearchableEditor() {
    val codeState = rememberCodeState(code = "", language = Language.KOTLIN)
    var showSearch by remember { mutableStateOf(false) }

    Column {
        if (showSearch) {
            FindReplaceBar(
                state = codeState,
                showReplace = true,
                onDismiss = { showSearch = false }
            )
        }
        NemoCodeEditor(
            state = codeState,
            modifier = Modifier.weight(1f)
        )
    }
}
```

### FindAndReplaceManager

The search logic behind both components. Use it directly to drive a search
from your own UI or from keyboard shortcuts.

| Property | Type | Description |
|----------|------|-------------|
| `findText` | `String` | Text to search for |
| `replaceText` | `String` | Text that replaces a match |
| `caseSensitive` | `Boolean` | Match upper and lower case exactly |
| `wholeWord` | `Boolean` | Match whole words only |
| `useRegex` | `Boolean` | Treat `findText` as a regular expression |
| `matches` | `List<IntRange>` | Ranges of the matches in the code |
| `currentMatchIndex` | `Int` | Index of the selected match, or -1 |

```kotlin
fun updateSearch()
fun goToNext()
fun goToPrevious()
fun replaceCurrent()
fun replaceAll()
fun hasMatches(): Boolean
fun clear()
```

---

## 🗂️ Tabs

Module: `nemo-tabs`. A `TabsManager` holds the open tabs in the order they were
opened. Each tab has its own `CodeState`. The manager does no file reading or
writing; that stays in your app.
```kotlin
implementation("io.github.ma7moud3ly:nemo-tabs:x.x.x")
```

### TabFile

Implement it on your own file class so it can be opened in a tab.
```kotlin
interface TabFile {
    val name: String   // text shown on the tab
    val path: String   // identifies the document; empty when not saved yet
}
```

### NemoTab

| Property | Type | Description |
|----------|------|-------------|
| `id` | `String` | Unique id of the tab |
| `tabFile` | `TabFile` | What the tab edits |
| `codeState` | `CodeState` | Editor state holding the tab's text |
| `title` | `String` | Name of the tab's file |
| `path` | `String` | Path of the tab's file |
| `content` | `String` | Text currently in the tab |
| `isDirty` | `Boolean` | Whether the tab has unsaved changes |
| `isNew` | `Boolean` | Whether the tab has text but no path yet |

### TabsManager

| Property | Type | Description |
|----------|------|-------------|
| `tabs` | `List<NemoTab>` | Open tabs, in order |
| `activeTab` | `NemoTab?` | The active tab |
| `activeTabFlow` | `Flow<NemoTab?>` | The active tab as a flow |
| `activeCodeState` | `CodeState?` | Code state of the active tab |

```kotlin
// Open and update
fun openTab(document: TabFile, content: String, language: Language, isDirty: Boolean = false): NemoTab
fun addTab(document: TabFile, content: String = "", language: Language = Language.BLANK, isDirty: Boolean = false): NemoTab
fun updateTab(tabId: String, document: TabFile, language: Language? = null)

// Find
fun getTab(tabId: String): NemoTab?
fun findTab(path: String): NemoTab?
fun getCodeState(tabId: String): CodeState?
fun getDirtyTabs(): List<NemoTab>

// Switch
fun switchTab(tabId: String)
fun switchToNextTab()
fun switchToPreviousTab()

// Close
fun closeTab(tabId: String): Boolean          // false when the tab is dirty
fun closeAllTabs(): List<NemoTab>             // returns the dirty tabs
fun closeOtherTabs(tabId: String): List<NemoTab>
fun closeTabsToRight(tabId: String): List<NemoTab>
fun forceCloseTab(tabId: String): Boolean
fun forceCloseAllTabs()

// Change tracking
fun commitChanges(tabId: String)              // Mark as saved
```

`openTab` switches to the open tab when a document with the same `path` is
already open; `addTab` always adds one. The close functions leave dirty tabs
open and report them, so the app can ask before calling a `forceClose` one.

### NemoTabs

The tab strip. Each tab shows an icon, its title, a dot when it has unsaved
changes, and a close button.
```kotlin
@Composable
fun NemoTabs(
    tabs: List<NemoTab>,
    activeTabId: String?,
    onSelect: (NemoTab) -> Unit,
    onClose: (NemoTab) -> Unit,
    modifier: Modifier = Modifier,
    closeLabel: String = "Close",
    icon: @Composable (NemoTab) -> Unit = { EditorTabIcon() }
)
```

### Example: Editor with Tabs
```kotlin
data class MyFile(
    override val name: String,
    override val path: String
) : TabFile

@Composable
fun TabbedEditor() {
    val tabsManager = remember { TabsManager() }
    val activeTab by tabsManager.activeTabFlow.collectAsState(null)

    LaunchedEffect(Unit) {
        tabsManager.openTab(
            document = MyFile(name = "Main.kt", path = "/project/Main.kt"),
            content = "fun main() {}",
            language = Language.KOTLIN
        )
    }

    Column {
        NemoTabs(
            tabs = tabsManager.tabs,
            activeTabId = activeTab?.id,
            onSelect = { tabsManager.switchTab(it.id) },
            onClose = { tab ->
                if (!tabsManager.closeTab(tab.id)) {
                    // The tab has unsaved changes: ask the user, then
                    // tabsManager.forceCloseTab(tab.id)
                }
            }
        )
        activeTab?.let { tab ->
            NemoCodeEditor(
                state = tab.codeState,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
```

After saving a tab's text, call `tabsManager.commitChanges(tab.id)` to clear its
unsaved-changes dot.

---

## 🌍 Supported Languages

```kotlin
enum class Language {
    KOTLIN, JAVA, PYTHON, MICRO_PYTHON, JAVASCRIPT, TYPESCRIPT,
    REACT_JSX, REACT_TSX, HTML, CSS, JSON, XML, MARKDOWN,
    C, CPP, C_HEADER, RUST, GO, SWIFT, DART, PHP, RUBY,
    SHELL, SQL, YAML, TOML, GRADLE, TEXT, BLANK
}
```

Any value can be passed to `CodeState`, but only three have a dedicated
implementation behind them. The rest fall back to a generic tokenizer and have
no formatter, error detector or completion provider.

| Capability | Kotlin | Python | MicroPython | Others |
|------------|--------|--------|-------------|--------|
| Syntax highlighting | ✅ | ✅ | ✅ | generic fallback |
| Code formatting | ✅ | ✅ | ✅ | ❌ |
| Autocomplete | ✅ | ✅ | ✅ | ❌ |
| Error detection | ✅ | ✅ | ✅ | ❌ |
| Auto-indentation | ✅ | ✅ | ✅ | Java, JavaScript only |

#### Syntax Highlighting
Color-coded keywords, types and classes, functions and methods, variables,
strings, numbers, comments, operators and punctuation.

#### Code Formatting
Smart indentation, bracket matching, whitespace normalisation, and
language-specific rules such as Python block indentation or Kotlin brace style.

#### Autocomplete
Keywords, built-in functions, types and classes, variables in scope, methods
after dot notation, and snippets.

#### Error Detection
- **Kotlin** — missing brackets, syntax errors
- **Python / MicroPython** — indentation errors, missing colons, syntax errors

---

## 📐 Indentation Guides

Vertical guides drawn at each indentation level. They matter most in
indentation-scoped languages, where nothing but whitespace closes a block.

Levels come from the indentation the file actually uses rather than multiples
of the tab size, so a line out of alignment with its siblings gains its own
guide:

```python
def foo():
   print(1)     # establishes the body at column 3
    print(2)    # deeper, so it gets a guide at column 3
```

A blank line keeps a guide running only while the block continues, and the
guide containing the caret is emphasised.

Enabled by default; control it with `showIndentGuides` on `EditorSettings`, or
`toggleIndentGuides()` at runtime. Guides assume space indentation — with
`useTabs = true`, alignment can drift.

---

## 🌐 Adding a Language

1. Implement `Tokenizer` and register it in `TokenizerFactory`
2. Optionally implement `CodeFormatter`, `ErrorDetector` and
   `CompletionProvider`, registering each in its matching factory
3. Add the case to `AutoIndentHandler` if the language needs indent rules

---

## 🎨 Themes

### Built-in Themes
```kotlin
// Dark themes
EditorThemes.NEMO_DARK
EditorThemes.NEMO_SUNSET
EditorThemes.VS_CODE_DARK
EditorThemes.MONOKAI
EditorThemes.DRACULA
EditorThemes.NIGHT_OWL
EditorThemes.SOLARIZED_DARK
EditorThemes.BREEZE
EditorThemes.CANDY
EditorThemes.CRIMSON
EditorThemes.FALCON
EditorThemes.MEADOW
EditorThemes.MIDNIGHT
EditorThemes.RAINDROP
EditorThemes.SUNSET

// Light themes
EditorThemes.NEMO_LIGHT
EditorThemes.VS_CODE_LIGHT
EditorThemes.GITHUB_LIGHT
EditorThemes.SOLARIZED_LIGHT
```

### Custom Theme
```kotlin
val myTheme = EditorTheme(
    name = "My Theme",
    dark = true,
    background = 0xFF1E1E1E,
    foreground = 0xFFD4D4D4,
    gutter = 0xFF252526,
    lineNumber = 0xFF858585,
    selection = 0xFF264F78,
    cursor = 0xFFAEAFAD,
    syntax = SyntaxColors(
        keyword = 0xFF569CD6,
        type = 0xFF4EC9B0,
        function = 0xFFDCDCAA,
        variable = 0xFF9CDCFE,
        string = 0xFFCE9178,
        number = 0xFFB5CEA8,
        comment = 0xFF6A9955,
        operator = 0xFFD4D4D4,
        punctuation = 0xFFD4D4D4,
        error = 0xFFF48771
    )
)

val settings = EditorSettings(theme = myTheme)
```

`SyntaxColors` also takes two optional colors for documentation comments such
as KDoc:

| Color | Used for | When not set |
|-------|----------|--------------|
| `docComment` | The text of a `/** */` comment | Same as `comment` |
| `docTag` | Tags and links inside it, such as `@param` and `[name]` | Same as `keyword` |
