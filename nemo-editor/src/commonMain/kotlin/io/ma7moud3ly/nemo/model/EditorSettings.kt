package io.ma7moud3ly.nemo.model

import androidx.compose.runtime.mutableStateOf
import io.ma7moud3ly.nemo.model.EditorThemes
import kotlin.math.max
import kotlin.math.min

/**
 * Configuration for the code editor
 *
 * @param theme Color theme for the editor
 * @param tabSize Number of spaces per tab (2-8)
 * @param useTabs Use tab character instead of spaces
 * @param showLineNumbers Display line numbers in the gutter
 * @param fontSize Font size
 * @param fontFamily Font family name (default: JetBrains Mono)
 * @param enableAutoIndent Automatically indent new lines
 * @param enableAutocomplete Show autocomplete suggestions
 * @param readOnly Make the editor non-editable
 */
data class EditorSettings(
    // Theme
    private val theme: EditorTheme = EditorThemes.VS_CODE_DARK,

    // Indentation
    private val tabSize: Int = 4,
    private val useTabs: Boolean = false,

    // Display
    private val showLineNumbers: Boolean = true,
    private val fontSize: Int = 14,
    val fontFamily: String = "JetBrains Mono",

    // Features
    private val enableAutoIndent: Boolean = true,
    private val enableAutocomplete: Boolean = true,
    private val readOnly: Boolean = false,
) {
    init {
        require(tabSize in 2..8) { "Tab size must be between 2 and 8" }
        require(fontSize in 8..32) { "Font size must be between 8 and 32" }
    }

    val themeState = mutableStateOf(theme)
    val tabSizeState = mutableStateOf(tabSize)
    val useTabsState = mutableStateOf(useTabs)
    val showLineNumbersState = mutableStateOf(showLineNumbers)
    val fontSizeState = mutableStateOf(fontSize)
    val enableAutoIndentState = mutableStateOf(enableAutoIndent)
    val enableAutocompleteState = mutableStateOf(enableAutocomplete)
    val readOnlyState = mutableStateOf(readOnly)

    fun toggleReadOnly() {
        readOnlyState.value = !readOnlyState.value
    }

    fun toggleLinesNumber() {
        showLineNumbersState.value = !showLineNumbersState.value
    }

    fun gesturesZoom(scale: Float) {
        fontSizeState.value = (fontSizeState.value * scale)
            .toInt()
            .coerceIn(FONT_SIZE_MIN, FONT_SIZE_MAX)
    }

    fun zoomIn(value: Int = 2) {
        fontSizeState.value = min(fontSizeState.value + value, FONT_SIZE_MAX)
    }

    fun zoomOut(value: Int = 2) {
        fontSizeState.value = max(fontSizeState.value - value, FONT_SIZE_MIN)
    }

    fun increaseTabSize(value: Int = 1) {
        tabSizeState.value = min(tabSizeState.value + value, TAB_SIZE_MAX)
    }

    fun decreaseTabSize(value: Int = 1) {
        tabSizeState.value = max(tabSizeState.value - value, TAB_SIZE_MIN)
    }

    fun setFontSize(value: Int) {
        fontSizeState.value = value.coerceIn(FONT_SIZE_MIN, FONT_SIZE_MAX)
    }


    fun setTabSize(value: Int) {
        tabSizeState.value = value.coerceIn(TAB_SIZE_MIN, TAB_SIZE_MAX)
    }

    fun copy() = EditorSettings(
        themeState.value,
        tabSizeState.value,
        useTabsState.value,
        showLineNumbersState.value,
        fontSizeState.value,
        fontFamily,
        enableAutoIndentState.value,
        enableAutocompleteState.value,
        readOnlyState.value
    )

    /**
     * Get the indent string based on settings
     */
    fun getIndentString(): String = if (useTabsState.value) "\t"
    else " ".repeat(tabSizeState.value)


    companion object {
        private const val FONT_SIZE_MAX = 50
        private const val FONT_SIZE_MIN = 8

        private const val TAB_SIZE_MAX = 16
        private const val TAB_SIZE_MIN = 2
    }
}