package io.ma7moud3ly.nemo.model

import androidx.compose.ui.graphics.Color

/**
 * App-level metadata describing a [Language] in the UI (new file dialog,
 * language pickers). Kept out of the editor library, which only needs the
 * [Language] enum itself.
 */
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
        "mpy" -> Language.MICRO_PYTHON
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
