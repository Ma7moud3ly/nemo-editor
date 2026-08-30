# 🐠 Nemo Editor

**Just keep coding**

A lightweight, fast, and beautiful code editor built with Kotlin Multiplatform and Compose Multiplatform. Dedicated language support for Kotlin, Python and MicroPython, and it opens and edits everything else.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Compose-1.7+-4285F4?logo=jetpackcompose&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Desktop%20%7C%20Web-blue)
![License](https://img.shields.io/badge/License-MIT-green)

---
## 📥 Download

- 🤖 [Android (Google Play)](https://play.google.com/store/apps/details?id=io.ma7moud3ly.nemo)
- 🍎 iOS (App Store) *(coming soon)*
- 🌐 [Web Version](https://nemo-editor.web.app/)
- 📦 [Latest Release (GitHub)](https://github.com/ma7moud3ly/nemo-editor/releases)

---

<image src="images/banner.png" />

## ✨ Features

### 🎨 Beautiful & Modern UI
- 19 gorgeous themes (Dark & Light variants)
- Animated logo and smooth transitions
- Clean, distraction-free interface
- Responsive design for all screen sizes

### 💻 Powerful Code Editor
- **Syntax Highlighting** - Kotlin, Python and MicroPython
- **Smart Autocomplete** - Context-aware suggestions with keyboard navigation
- **Error Detection** - Real-time syntax error highlighting
- **Code Formatting** - Auto-format your code beautifully
- **Auto-Indentation** - Intelligent indenting based on language
- **Indentation Guides** - Vertical guides that reveal misaligned lines
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
./gradlew :androidApp:installGmsDebug

# Run on Web
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

---

## 📦 Using Nemo Editor in Your KMP Project

### 📦 Installation
```kotlin
commonMain.dependencies {
    implementation("io.github.ma7moud3ly:nemo-editor:1.0.4")
}
```

### Example: Basic Kotlin Editor

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
        settings = editorSettings,
        modifier = Modifier.fillMaxSize()
    )
}
```

See [complete documentation](docs/NemoCodeEditor.md) for more examples.

---

## 🌍 Supported Languages

Three languages have dedicated support their own tokenizer, formatter, error
detector and completion provider:

| Language | Extensions | Highlighting | Formatting | Autocomplete | Errors |
|----------|-----------|--------------|------------|--------------|--------|
| Kotlin | .kt, .kts | ✅ | ✅ | ✅ | ✅ |
| Python | .py | ✅ | ✅ | ✅ | ✅ |
| MicroPython | .mpy | ✅ | ✅ | ✅ | ✅ |

Everything else opens and edits normally; Java, JavaScript, C/C++, HTML, CSS,
JSON, XML, Markdown, Shell, SQL and more can be created and edited, and the
file browser recognizes them, but they fall back to a generic tokenizer and
have no formatter, error detection or completion of their own.

Adding a language means implementing `Tokenizer`, and optionally
`CodeFormatter`, `ErrorDetector` and `CompletionProvider`, then registering it
in the matching factory. Contributions welcome.

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

The app lives in `:androidApp`; `:composeApp` is a shared library and produces
no APK. There are two flavours: `gms` includes Firebase analytics and
crashlytics, `default` is free of them.

```bash
# Debug APK
./gradlew :androidApp:assembleGmsDebug

# Release APK
./gradlew :androidApp:assembleGmsRelease

# Analytics-free build
./gradlew :androidApp:assembleDefaultRelease

# Output: androidApp/build/outputs/apk/
```

### Desktop

```bash
# Windows
./gradlew :composeApp:packageReleaseMsi
./gradlew :composeApp:packageReleaseExe

# macOS
./gradlew :composeApp:packageReleaseDmg

# Linux
./gradlew :composeApp:packageReleaseDeb
./gradlew :composeApp:packageReleaseRpm

# Output: composeApp/build/compose/binaries/main-release/
```

Each installer is built by `jpackage` on its own platform. The RPM target
additionally needs `rpmbuild` on the build machine (`apt install rpm` on
Debian-based systems).

### Web (WASM)
```bash
# Development
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Production
./gradlew :composeApp:wasmJsBrowserDistribution
# Output: composeApp/build/dist/wasmJs/productionExecutable/
```

### Library

```bash
# Publish to Maven Local for use in another project
./gradlew :nemo-editor:publishToMavenLocal
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