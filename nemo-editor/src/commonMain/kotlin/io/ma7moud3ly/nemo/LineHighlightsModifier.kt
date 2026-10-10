package io.ma7moud3ly.nemo

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import io.ma7moud3ly.nemo.model.LineHighlight

/**
 * How far a band is drawn past each side of this node, in pixels. The
 * scrolling container around the text clips it at the visible edges, so the
 * band always reaches both of them, whatever padding surrounds the text.
 */
private const val OVERDRAW = 100_000f

/**
 * Draws a colored band behind each highlighted line of this node's text.
 *
 * The parameters are lambdas so they are read in the draw phase: changing the
 * highlights repaints the bands without recomposing the editor.
 *
 * @param highlights the lines to highlight
 * @param textLayout layout of the text; the top and bottom of each band are
 *   read from it. Lines it does not have are skipped.
 * @param defaultColor color of a highlight that has no color of its own
 */
internal fun Modifier.lineHighlights(
    highlights: () -> List<LineHighlight>,
    textLayout: () -> TextLayoutResult?,
    defaultColor: Color
): Modifier = drawBehind {
    val layout = textLayout() ?: return@drawBehind
    highlights().forEach { highlight ->
        val lineIndex = highlight.line - 1
        if (lineIndex !in 0 until layout.lineCount) return@forEach

        val top = layout.getLineTop(lineIndex)
        val bottom = layout.getLineBottom(lineIndex)
        drawRect(
            color = highlight.color?.let { Color(it) } ?: defaultColor,
            topLeft = Offset(-OVERDRAW, top),
            size = Size(size.width + 2 * OVERDRAW, bottom - top)
        )
    }
}
