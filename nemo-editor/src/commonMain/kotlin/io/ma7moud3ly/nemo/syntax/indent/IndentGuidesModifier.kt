package io.ma7moud3ly.nemo.syntax.indent

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.round

/**
 * Draws vertical indentation guides behind the content of this node.
 *
 * Every parameter is a lambda so the values are read in the draw phase: moving
 * the caret repaints the active guide without triggering recomposition of the
 * editor.
 *
 * @param guides guides of the current document, or `null` to draw nothing.
 * @param activeLine zero-based caret line; the guide containing it is
 *   emphasised with [activeColor]. Pass `-1` to disable the highlight.
 * @param textLayout layout of the rendered code, used for exact row bounds.
 * @param charWidth advance width of one monospace character, in pixels.
 * @param color color of an ordinary guide.
 * @param activeColor color of the guide the caret sits inside.
 * @param strokeWidth thickness of a guide.
 */
fun Modifier.indentGuides(
    guides: () -> IndentGuides?,
    activeLine: () -> Int,
    textLayout: () -> TextLayoutResult?,
    charWidth: () -> Float,
    color: Color,
    activeColor: Color,
    strokeWidth: Dp = 1.dp
): Modifier = drawBehind {
    val model = guides() ?: return@drawBehind
    if (model.isEmpty) return@drawBehind

    val layout = textLayout() ?: return@drawBehind
    val advance = charWidth()
    if (advance <= 0f || layout.lineCount == 0) return@drawBehind

    val stroke = strokeWidth.toPx().coerceAtLeast(1f)
    val lastLine = layout.lineCount - 1
    val active = model.activeGuide(activeLine())

    model.guides.forEach { guide ->
        val top = layout.getLineTop(guide.startLine.coerceIn(0, lastLine))
        val bottom = layout.getLineBottom(guide.endLine.coerceIn(0, lastLine))
        if (bottom <= top) return@forEach

        // Snap to whole pixels, then bias by half a stroke so a guide sitting on
        // column 0 is not clipped by the left edge of the text area.
        val x = round(guide.column * advance) + stroke / 2f

        drawLine(
            color = if (guide == active) activeColor else color,
            start = Offset(x, top),
            end = Offset(x, bottom),
            strokeWidth = stroke,
            cap = StrokeCap.Butt
        )
    }
}
