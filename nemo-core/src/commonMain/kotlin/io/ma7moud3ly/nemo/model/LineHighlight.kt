package io.ma7moud3ly.nemo.model

/**
 * A colored background behind one line of the code.
 *
 * @param line the line number, one-based
 * @param color ARGB color of the background, in the same form as the theme
 *   colors. When null, the editor uses a color from its theme.
 */
data class LineHighlight(
    val line: Int,
    val color: Long? = null
)
