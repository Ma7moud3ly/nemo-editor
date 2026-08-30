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
    implementation("io.github.ma7moud3ly:nemo-editor:1.0.4")
}
```

---

## 🎯 Core API

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
| `contentChanged` | `Boolean` | Whether there are unsaved changes |

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
    fontFamily: String = "JetBrains Mono",
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
