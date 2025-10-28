package io.ma7moud3ly.nemo.model

import androidx.compose.ui.graphics.Color

enum class Language {
    KOTLIN,
    JAVA,
    PYTHON,
    JAVASCRIPT,
    TYPESCRIPT,
    REACT_JSX,
    REACT_TSX,
    HTML,
    CSS,
    JSON,
    XML,
    MARKDOWN,
    C,
    CPP,
    C_HEADER,
    RUST,
    GO,
    SWIFT,
    DART,
    PHP,
    RUBY,
    SHELL,
    SQL,
    YAML,
    TOML,
    GRADLE,
    TEXT,
    BLANK
}

data class LanguageDetails(
    val name: String,
    val language: Language,
    val extension: String,
    val icon: String,
    val color: Color,
    val description: String
)


fun String.asLanguage(): Language {
    return when (this) {
        "kt", "kts" -> Language.KOTLIN
        "java" -> Language.JAVA
        "py" -> Language.PYTHON
        "js", "jsx" -> Language.JAVASCRIPT
        "cpp", "cc", "cxx", "c", "h", "hpp" -> Language.CPP
        "html", "htm" -> Language.HTML
        "css", "scss", "sass" -> Language.CSS
        "json" -> Language.JSON
        "xml" -> Language.XML
        "md", "markdown" -> Language.MARKDOWN
        else -> Language.BLANK
    }
}


