package io.ma7moud3ly.nemo

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.ma7moud3ly.nemo.model.LineHighlight

/**
 * The line numbers beside the code.
 *
 * Only the numbers of the visible lines are drawn, each on the baseline of its
 * line in [textLayout], so the gutter costs the same however long the document
 * is. Everything it draws is read in the draw phase, so scrolling, moving the
 * caret and changing highlights only repaint it.
 *
 * @param totalLines number of lines; the gutter is as wide as the longest number
 * @param textLayout layout of the code, which places each number
 * @param scrollState vertical scroll of the code
 * @param textTop where the first line starts inside the scrolling code, in pixels
 * @param currentLine the caret's line (1-based), drawn in [activeColor]
 * @param highlights the highlighted lines, drawn as a band behind their numbers
 * @param onLineClick called with the line (1-based) of a clicked number
 */
@Composable
internal fun LineNumberGutter(
    totalLines: Int,
    textLayout: () -> TextLayoutResult?,
    scrollState: ScrollState,
    textTop: Float,
    currentLine: () -> Int,
    highlights: () -> List<LineHighlight>,
    style: TextStyle,
    color: Color,
    activeColor: Color,
    highlightColor: Color,
    background: Color,
    onLineClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer(cacheSize = 256)
    val density = LocalDensity.current
    val padding = with(density) { 4.dp.toPx() }

    // Wide enough for the longest number, and never narrower than two digits
    val digits = maxOf(2, totalLines.toString().length)
    val numberWidth = remember(digits, style) {
        textMeasurer.measure("9".repeat(digits), style).size.width
    }
    val width = with(density) { (numberWidth + 2 * padding).toDp() }

    val onClick by rememberUpdatedState(onLineClick)
    val top by rememberUpdatedState(textTop)

    Box(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .background(background)
            .clipToBounds()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val layout = textLayout() ?: return@detectTapGestures
                    val y = offset.y + scrollState.value - top
                    onClick(layout.getLineForVerticalPosition(y) + 1)
                }
            }
            .drawBehind {
                val layout = textLayout() ?: return@drawBehind
                if (layout.lineCount == 0) return@drawBehind

                val scroll = scrollState.value
                val first = layout.getLineForVerticalPosition(scroll - top)
                val last = layout.getLineForVerticalPosition(scroll - top + size.height)
                val active = currentLine() - 1
                val bands = highlights().associate { it.line - 1 to it.color }

                for (line in first..last) {
                    val lineTop = top + layout.getLineTop(line) - scroll
                    val lineBottom = top + layout.getLineBottom(line) - scroll
                    if (line in bands) {
                        drawRect(
                            color = bands[line]?.let { Color(it) } ?: highlightColor,
                            topLeft = Offset(0f, lineTop),
                            size = Size(size.width, lineBottom - lineTop)
                        )
                    }
                    if (line >= totalLines) continue

                    val number = textMeasurer.measure(
                        text = (line + 1).toString(),
                        style = style.copy(color = if (line == active) activeColor else color)
                    )
                    val y = top + layout.getLineBaseline(line) - scroll - number.firstBaseline
                    drawText(number, topLeft = Offset(size.width - padding - number.size.width, y))
                }
            }
    )
}
