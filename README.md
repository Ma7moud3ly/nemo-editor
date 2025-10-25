# 🐠 Nemo Editor

**Just keep coding**

A lightweight, fast, and beautiful code editor built with Kotlin Multiplatform and Compose Multiplatform. Supporting 19+ programming languages with syntax highlighting, autocomplete, and more!

![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Compose-1.7+-4285F4?logo=jetpackcompose&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web-blue)
![License](https://img.shields.io/badge/License-MIT-green)

---
## 📥 Download

- 🤖 [Android (Google Play)](https://play.google.com/store/apps/details?id=com.ma7moud3ly.nemo)
- 🍎 iOS (App Store) *(coming soon)*
- 🌐 [Web Version](https://nemo-editor.web.app/)
- 📦 [Latest Release (GitHub)](https://github.com/ma7moud3ly/nemo-editor/releases)

---

<image src="images/banner.png">

## ✨ Features

### 🎨 Beautiful & Modern UI
- 19 gorgeous themes (Dark & Light variants)
- Animated logo and smooth transitions
- Clean, distraction-free interface
- Responsive design for all screen sizes

### 💻 Powerful Code Editor
- **Syntax Highlighting** - 19+ languages supported
- **Smart Autocomplete** - Context-aware suggestions with keyboard navigation
- **Error Detection** - Real-time syntax error highlighting
- **Code Formatting** - Auto-format your code beautifully
- **Auto-Indentation** - Intelligent indenting based on language
- **Multi-tab Editing** - Work on multiple files simultaneously
- **Find & Replace** - Regex support, case-sensitive, whole word matching
- **Line Numbers** - Toggle-able with gutter highlighting
- **Undo/Redo** - Full history support (up to 100 actions)
- **Read-only Mode** - View files without editing

### 📁 File Management
- File explorer with CRUD operations
- Open files and folders
- Create, rename, delete files/folders
- Recent files tracking
- File details with metadata
- Cross-platform file system support

### ⚡ Performance
- Lightweight and fast
- Minimal resource usage
- Smooth scrolling and editing
- Optimized rendering

### 🌐 Cross-Platform
- **Android** - Native app
- **iOS** - Native app
- **Desktop** - Windows, macOS, Linux
- **Web** - WASM support

---

## 🚀 Quick Start

### Prerequisites
- JDK 17 or higher
- Android Studio (for Android development)
- Xcode (for iOS development, macOS only)

### Clone & Run
```bash
# Clone the repository
git clone https://github.com/Ma7moud3ly/nemo-editor.git
cd nemo-editor

# Run on Desktop
./gradlew :composeApp:run

# Run on Android
./gradlew :composeApp:installDebug

# Run on Web
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

---

## 📦 Using Nemo Editor in Your KMP Project

## 📚 Examples

### Example: Basic Kotlin Editor

```kotlin
@Composable
fun MyEditor() {
    val codeState = rememberCodeState(
        code = "fun main() {\n    println(\"Hello\")\n}",
        language = Language.KOTLIN
    )
    NemoCodeEditor(codeState = codeState)
}
```

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


    val settings = remember {
        EditorSettings(
            theme = EditorThemes.NEMO_DARK,
            tabSize = 4,
        )
    }

    NemoCodeEditor(
        codeState = codeState,
        editorSettings = settings,
        modifier = Modifier.fillMaxSize()
    )
}
```

See [complete documentation](docs/NemoCodeEditor.md) for more examples.

---

## 🌍 Supported Languages

| Language | Extensions | Highlighting | Formatting | Autocomplete | Errors |
|----------|-----------|--------------|------------|--------------|--------|
| Kotlin | .kt, .kts | ✅ | ✅ | ✅ | ✅ |
| Python | .py | ✅ | ✅ | ✅ | ✅ |
| Java | .java | ✅ | ✅ | ❌ | ✅ |
| JavaScript | .js | ✅ | ✅ | ❌ | ✅ |
| TypeScript | .ts | ✅ | ✅ | ❌ | ✅ |
| C | .c, .h | ✅ | ✅ | ❌ | ❌ |
| C++ | .cpp, .hpp | ✅ | ✅ | ❌ | ❌ |
| C# | .cs | ✅ | ✅ | ❌ | ❌ |
| Go | .go | ✅ | ✅ | ❌ | ❌ |
| Rust | .rs | ✅ | ✅ | ❌ | ❌ |
| Swift | .swift | ✅ | ✅ | ❌ | ❌ |
| PHP | .php | ✅ | ✅ | ❌ | ❌ |
| Ruby | .rb | ✅ | ✅ | ❌ | ❌ |
| HTML | .html, .htm | ✅ | ✅ | ❌ | ❌ |
| CSS | .css, .scss | ✅ | ✅ | ❌ | ❌ |
| XML | .xml | ✅ | ✅ | ❌ | ❌ |
| JSON | .json | ✅ | ✅ | ❌ | ❌ |
| Markdown | .md | ✅ | ❌ | ❌ | ❌ |
| SQL | .sql | ✅ | ✅ | ❌ | ❌ |
| Shell | .sh, .bash | ✅ | ✅ | ❌ | ❌ |

---

## 🎨 Available Themes

**Dark Themes:**
- Nemo Dark (Default)
- Monokai
- Dracula
- One Dark
- Nord
- Gruvbox Dark
- Solarized Dark
- Material Palenight
- Atom One Dark
- Tokyo Night

**Light Themes:**
- Nemo Light
- GitHub Light
- Solarized Light
- Gruvbox Light
- Atom One Light
- Material Lighter
- Quiet Light
- Light+
- Tomorrow

---

## ⌨️ Keyboard Shortcuts

### General
| Windows/Linux | macOS | Action |
|---------------|-------|--------|
| Ctrl + N | ⌘ + N | New File |
| Ctrl + O | ⌘ + O | Open File |
| Ctrl + Shift + O | ⌘ + Shift + O | Open Folder |
| Ctrl + S | ⌘ + S | Save |
| Ctrl + Shift + S | ⌘ + Shift + S | Save As |
| Ctrl + W | ⌘ + W | Close Tab |
| Ctrl + Shift + W | ⌘ + Shift + W | Close All Tabs |

### Edit
| Windows/Linux | macOS | Action |
|---------------|-------|--------|
| Ctrl + Z | ⌘ + Z | Undo |
| Ctrl + Y | ⌘ + Y | Redo |
| Ctrl + D | ⌘ + D | Duplicate Line |
| Ctrl + L | ⌘ + L | Delete Line |
| Ctrl + / | ⌘ + / | Toggle Comment |
| Ctrl + ] | ⌘ + ] | Indent |
| Ctrl + [ | ⌘ + [ | Unindent |

### Search & Format
| Windows/Linux | macOS | Action |
|---------------|-------|--------|
| Ctrl + F | ⌘ + F | Find |
| Ctrl + H | ⌘ + H | Find & Replace |
| Ctrl + Shift + F | ⌘ + Shift + F | Format Code |

### View
| Windows/Linux | macOS | Action |
|---------------|-------|--------|
| Ctrl + = | ⌘ + = | Zoom In |
| Ctrl + - | ⌘ + - | Zoom Out |
| Ctrl + B | ⌘ + B | Toggle Sidebar |
| Ctrl + Tab | ⌘ + Tab | Next Tab |
| Ctrl + Shift + Tab | ⌘ + Shift + Tab | Previous Tab |

### Autocomplete
- **↓** - Select next
- **↑** - Select previous
- **Enter** or **Tab** - Accept
- **Esc** - Dismiss

See [complete shortcuts guide](docs/keyboard_shortcuts_readme.md).

---

## 🏗️ Building from Source

### Android
```bash
# Debug APK
./gradlew :composeApp:assembleDebug

# Release APK
./gradlew :composeApp:assembleRelease

# Output: composeApp/build/outputs/apk/
```

### Desktop

**Windows (MSI)**
```bash
./gradlew :composeApp:packageMsi
# Output: composeApp/build/compose/binaries/main/msi/
```

**macOS (DMG)**
```bash
./gradlew :composeApp:packageDmg
# Output: composeApp/build/compose/binaries/main/dmg/
```

**Linux (DEB)**
```bash
./gradlew :composeApp:packageDeb
# Output: composeApp/build/compose/binaries/main/deb/
```

### Web (WASM)
```bash
# Development
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Production
./gradlew :composeApp:wasmJsBrowserDistribution
# Output: composeApp/build/dist/wasmJs/productionExecutable/
```

---

## 📖 Documentation

- **[NemoCodeEditor API](docs/NemoCodeEditor.md)** - Complete API reference
- **[Keyboard Shortcuts](docs/keyboard_shortcuts_readme.md.md)** - Complete shortcuts guide

---

## 🤝 Contributing

Contributions are welcome! Here's how:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Guidelines
- Follow Kotlin coding conventions
- Write clear commit messages
- Add tests for new features
- Update documentation
- Ensure all platforms build

---

## 📄 License

MIT License - see [LICENSE](LICENSE) file.

---

## 🙏 Acknowledgments

- Built with [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- UI by [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)
- Inspired by VSCode, Sublime Text, and other great editors

---

## 📧 Contact

- **Author:** Mahmoud Aly
- **GitHub:** [@Ma7moud3ly](https://github.com/Ma7moud3ly)
- **Project:** [Nemo Editor](https://github.com/Ma7moud3ly/nemo-editor)
- **Issues:** [Report a bug](https://github.com/Ma7moud3ly/nemo-editor/issues)

---

## ⭐ Show Your Support

If you find Nemo Editor useful:
- ⭐ Star the repository
- 🐛 Report bugs
- 💡 Suggest features
- 🤝 Contribute code
- 📢 Share with others

---

**Made with ❤️ using Kotlin Multiplatform**

**Nemo Editor - Just keep coding 🐠**

[⭐ Star on GitHub](https://github.com/Ma7moud3ly/nemo-editor) • [📖 Documentation](docs/NemoCodeEditor.md) • [🐛 Report Bug](https://github.com/Ma7moud3ly/nemo-editor/issues) • [💬 Discussions](https://github.com/Ma7moud3ly/nemo-editor/discussions)