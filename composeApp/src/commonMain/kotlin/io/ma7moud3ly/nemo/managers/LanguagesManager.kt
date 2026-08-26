package io.ma7moud3ly.nemo.managers

import androidx.compose.ui.graphics.Color
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.model.LanguageDetails

class LanguagesManager {
    val supportedLanguages = listOf(
        LanguageDetails("Kotlin", Language.KOTLIN, "kt", "🟣", Color(0xFF7F52FF), "Modern JVM language"),
        LanguageDetails("Java", Language.JAVA, "java", "☕", Color(0xFFE76F00), "Object-oriented programming"),
        LanguageDetails("Python", Language.PYTHON, "py", "🐍", Color(0xFF3776AB), "General-purpose scripting"),
        LanguageDetails("MicroPython", Language.MICRO_PYTHON, "mpy", "🤖", Color(0xFF2B7489), "Python for microcontrollers"),
        LanguageDetails("JavaScript", Language.JAVASCRIPT, "js", "🟨", Color(0xFFF7DF1E), "Web scripting language"),
        LanguageDetails("TypeScript", Language.TYPESCRIPT, "ts", "🔷", Color(0xFF3178C6), "Typed JavaScript"),
        LanguageDetails("React JSX", Language.REACT_JSX, "jsx", "⚛️", Color(0xFF61DAFB), "React components"),
        LanguageDetails("React TSX", Language.REACT_TSX, "tsx", "⚛️", Color(0xFF61DAFB), "React with TypeScript"),
        LanguageDetails("HTML", Language.HTML, "html", "🌐", Color(0xFFE34C26), "Markup language"),
        LanguageDetails("CSS", Language.CSS, "css", "🎨", Color(0xFF1572B6), "Styling language"),
        LanguageDetails("JSON", Language.JSON, "json", "📋", Color(0xFF000000), "Data interchange format"),
        LanguageDetails("XML", Language.XML, "xml", "📄", Color(0xFFFF6600), "Markup language"),
        LanguageDetails("Markdown", Language.MARKDOWN, "md", "📝", Color(0xFF083FA1), "Lightweight markup"),
        LanguageDetails("C", Language.C, "c", "🔵", Color(0xFFA8B9CC), "System programming"),
        LanguageDetails("C++", Language.CPP, "cpp", "🔵", Color(0xFF00599C), "Object-oriented C"),
        LanguageDetails("C Header", Language.C_HEADER, "h", "🔵", Color(0xFFA8B9CC), "C/C++ header file"),
        LanguageDetails("Rust", Language.RUST, "rs", "🦀", Color(0xFFCE422B), "Systems programming"),
        LanguageDetails("Go", Language.GO, "go", "🐹", Color(0xFF00ADD8), "Google's language"),
        LanguageDetails("Swift", Language.SWIFT, "swift", "🍎", Color(0xFFFA7343), "iOS development"),
        LanguageDetails("Dart", Language.DART, "dart", "🎯", Color(0xFF0175C2), "Flutter language"),
        LanguageDetails("PHP", Language.PHP, "php", "🐘", Color(0xFF777BB4), "Server-side scripting"),
        LanguageDetails("Ruby", Language.RUBY, "rb", "💎", Color(0xFFCC342D), "Dynamic programming"),
        LanguageDetails("Shell Script", Language.SHELL, "sh", "🐚", Color(0xFF89E051), "Unix shell scripting"),
        LanguageDetails("SQL", Language.SQL, "sql", "🗄️", Color(0xFFE38C00), "Database queries"),
        LanguageDetails("YAML", Language.YAML, "yaml", "📦", Color(0xFFCB171E), "Configuration format"),
        LanguageDetails("TOML", Language.TOML, "toml", "⚙️", Color(0xFF9C4121), "Configuration format"),
        LanguageDetails("Gradle", Language.GRADLE, "gradle", "🐘", Color(0xFF02303A), "Build configuration"),
        LanguageDetails("Text", Language.TEXT, "txt", "📄", Color(0xFF666666), "Plain text file")
    )
}