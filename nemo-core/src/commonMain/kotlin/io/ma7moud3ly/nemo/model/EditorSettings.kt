package io.ma7moud3ly.nemo.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * Configuration for the code editor
 *
 * @param theme Color theme for the editor
 * @param tabSize Number of spaces per tab (2-8)
 * @param useTabs Use tab character instead of spaces
 * @param showLineNumbers Display line numbers in the gutter
 * @param showIndentGuides Draw vertical guides at each indentation level
 * @param fontSize Font size
 * @param fontFamily Font of the code, the line numbers and the autocomplete popup
 * @param enableAutoIndent Automatically indent new lines
 * @param enableAutocomplete Show autocomplete suggestions
 * @param readOnly Make the editor non-editable
 * @param contentPadding Space between the code and the edges of the editor.
 *   The line numbers use its top and bottom values, so they stay aligned
 *   with their lines.
 * @param showScrollbars Show a scroll bar while the code is larger than the editor
 */
data class EditorSettings(
    // Theme
    private val theme: EditorTheme = EditorThemes.VS_CODE_DARK,

    // Indentation
    private val tabSize: Int = 4,
    private val useTabs: Boolean = false,

    // Display
    private val showLineNumbers: Boolean = true,
    private val showIndentGuides: Boolean = true,
    private val fontSize: Int = 14,
    private val fontFamily: FontFamily = FontFamily.Monospace,

    // Features
    private val enableAutoIndent: Boolean = true,
    private val enableAutocomplete: Boolean = true,
    private val readOnly: Boolean = false,

    // Layout
    private val contentPadding: PaddingValues = PaddingValues(start = 4.dp, top = 4.dp, end = 4.dp),
    private val showScrollbars: Boolean = true,
) {
    init {
        require(tabSize in 2..8) { "Tab size must be between 2 and 8" }
        require(fontSize in 8..32) { "Font size must be between 8 and 32" }
    }

    val themeState = mutableStateOf(theme)
    val tabSizeState = mutableStateOf(tabSize)
    val useTabsState = mutableStateOf(useTabs)
    val showLineNumbersState = mutableStateOf(showLineNumbers)
    val showIndentGuidesState = mutableStateOf(showIndentGuides)
    val fontSizeState = mutableStateOf(fontSize)
    val fontFamilyState = mutableStateOf(fontFamily)
    val enableAutoIndentState = mutableStateOf(enableAutoIndent)
    val enableAutocompleteState = mutableStateOf(enableAutocomplete)
    val readOnlyState = mutableStateOf(readOnly)
    val contentPaddingState = mutableStateOf(contentPadding)
    val showScrollbarsState = mutableStateOf(showScrollbars)

    fun toggleReadOnly() {
        readOnlyState.value = !readOnlyState.value
    }

    fun toggleLinesNumber() {
        showLineNumbersState.value = !showLineNumbersState.value
    }

    fun toggleIndentGuides() {
        showIndentGuidesState.value = !showIndentGuidesState.value
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
        showIndentGuidesState.value,
        fontSizeState.value,
        fontFamilyState.value,
        enableAutoIndentState.value,
        enableAutocompleteState.value,
        readOnlyState.value,
        contentPaddingState.value,
        showScrollbarsState.value
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