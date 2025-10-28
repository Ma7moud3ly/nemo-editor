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
    implementation("io.github.ma7moud3ly:nemo-editor:1.0.2")
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
    settings: EditorSettings,
    modifier: Modifier = Modifier
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
    code: String = "",
    language: Language = Language.KOTLIN
)
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `code` | `String` | Current code content |
| `language` | `Language` | Programming language |

### Methods
```kotlin
fun undo()
fun redo()
fun canUndo(): Boolean
fun canRedo(): Boolean
fun clearHistory()
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
  <img src="https://raw.githubusercontent.com/Ma7moud3ly/kmp-code-editor/refs/heads/master/images/docs/exmple1.png" alt="Basic Kotlin Editor" width="400"/>
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
  <img src="https://raw.githubusercontent.com/Ma7moud3ly/kmp-code-editor/refs/heads/master/images/docs/exmple2.png" alt="Basic Kotlin Editor" width="400"/>
  <p><em>Python editor</em></p>
</div>

---

## ⚙️ EditorSettings

Controls editor appearance and behavior.

### Constructor
```kotlin
EditorSettings(
    theme: EditorTheme = EditorThemes.NEMO_DARK,
    fontSize: Int = 14,
    tabSize: Int = 4,
    useTabs: Boolean = false,
    showLineNumbers: Boolean = true,
    enableAutocomplete: Boolean = true,
    enableAutoIndent: Boolean = true,
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
| `enableAutocompleteState` | `MutableState<Boolean>` | Enable autocomplete |
| `enableAutoIndentState` | `MutableState<Boolean>` | Enable auto-indent |
| `readOnlyState` | `MutableState<Boolean>` | Read-only mode |

### Methods
```kotlin
fun zoomIn()
fun zoomOut()
fun toggleLinesNumber()
fun toggleReadOnly()
```

---

## 🌍 Supported Languages
```kotlin
enum class Language {
    KOTLIN, JAVA, PYTHON, JAVASCRIPT, TYPESCRIPT,
    C, CPP, CSHARP, GO, RUST, SWIFT, PHP, RUBY,
    HTML, CSS, XML, JSON, MARKDOWN, SQL, SHELL
}
```

#### ✅ Syntax Highlighting
All languages support full syntax highlighting with color-coded:
- Keywords
- Types and classes
- Functions and methods
- Variables
- Strings and characters
- Numbers
- Comments
- Operators
- Punctuation

#### ✅ Code Formatting
Supported languages include:
- Smart indentation
- Bracket matching
- Line wrapping
- Whitespace normalization
- Language-specific rules (e.g., Python indentation, Kotlin/Java style)

**Available for:** Kotlin, Python

#### ✅ Autocomplete
Context-aware code suggestions including:
- Keywords
- Built-in functions
- Types and classes
- Variables in scope
- Methods after dot notation
- Smart snippets

**Available for:** Kotlin, Python


#### ✅ Error Detection
Real-time syntax error highlighting:
- **Kotlin:** Missing brackets, semicolons, syntax errors
- **Python:** Indentation errors, missing colons, syntax errors
- **Java:** Missing semicolons, bracket mismatches, syntax errors
- **JavaScript/TypeScript:** Missing brackets, semicolons, syntax errors

**Available for:** Kotlin, Python

---

## 🎨 Themes

### Built-in Themes
```kotlin
// Dark themes
EditorThemes.NEMO_DARK
EditorThemes.MONOKAI
EditorThemes.DRACULA
EditorThemes.ONE_DARK
EditorThemes.NORD
EditorThemes.GRUVBOX_DARK
EditorThemes.SOLARIZED_DARK
EditorThemes.MATERIAL_PALENIGHT
EditorThemes.ATOM_ONE_DARK
EditorThemes.TOKYO_NIGHT

// Light themes
EditorThemes.NEMO_LIGHT
EditorThemes.GITHUB_LIGHT
EditorThemes.SOLARIZED_LIGHT
EditorThemes.GRUVBOX_LIGHT
EditorThemes.ATOM_ONE_LIGHT
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