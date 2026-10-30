package io.ma7moud3ly.nemo.syntax.indent

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
 * @param textLayout layout of the rendered code. Both the row bounds and the x
 *   of every guide are read from it, so guides stay aligned with the glyphs on
 *   any platform and under any font the target actually resolves.
 * @param color color of an ordinary guide.
 * @param activeColor color of the guide the caret sits inside.
 * @param strokeWidth thickness of a guide.
 */
fun Modifier.indentGuides(
    guides: () -> IndentGuides?,
    activeLine: () -> Int,
    textLayout: () -> TextLayoutResult?,
    color: Color,
    activeColor: Color,
    strokeWidth: Dp = 1.dp
): Modifier = drawBehind {
    val model = guides() ?: return@drawBehind
    if (model.isEmpty) return@drawBehind

    val layout = textLayout() ?: return@drawBehind
    if (layout.lineCount == 0) return@drawBehind

    val stroke = strokeWidth.toPx().coerceAtLeast(1f)
    val lastLine = layout.lineCount - 1
    val textLength = layout.layoutInput.text.length
    val active = model.activeGuide(activeLine())

    model.guides.forEach { guide ->
        val top = layout.getLineTop(guide.startLine.coerceIn(0, lastLine))
        val bottom = layout.getLineBottom(guide.endLine.coerceIn(0, lastLine))
        if (bottom <= top) return@forEach

        // Ask the layout where that character sits rather than multiplying a
        // measured advance: a separately measured width drifts whenever the
        // resolved font differs from the one measured, which is what happens on
        // the web target while fonts are still loading.
        val anchor = guide.anchorLine.coerceIn(0, lastLine)
        val offset = (layout.getLineStart(anchor) + guide.charOffset)
            .coerceIn(0, textLength)

        // Bias by half a stroke so a guide on column 0 is not clipped by the
        // left edge of the text area.
        val x = layout.getHorizontalPosition(offset, usePrimaryDirection = true) + stroke / 2f

        drawLine(
            color = if (guide == active) activeColor else color,
            start = Offset(x, top),
            end = Offset(x, bottom),
            strokeWidth = stroke,
            cap = StrokeCap.Butt
        )
    }
}
