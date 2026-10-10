package io.ma7moud3ly.nemo

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/** Width of a vertical bar, and height of a horizontal one. */
private val Thickness = 10.dp

/** Space between the thumb and the edges of its bar. */
private val ThumbPadding = 2.dp

/** The thumb never gets shorter than this, so it stays easy to grab. */
private val MinThumbLength = 24.dp

/**
 * A scroll bar for [scrollState], meant to be laid over the edge of the
 * scrolling area.
 *
 * Its thumb shows which part of the content is visible and can be dragged to
 * scroll. Nothing is drawn while the content fits in the view.
 *
 * @param scrollState the scroll position the bar shows and changes
 * @param orientation vertical for a bar along the side, horizontal for one
 *   along the bottom
 * @param color color of the thumb
 * @param modifier places the bar; it fills the side it is aligned to
 */
@Composable
internal fun EditorScrollbar(
    scrollState: ScrollState,
    orientation: Orientation,
    color: Color,
    modifier: Modifier = Modifier
) {
    val maxScroll = scrollState.maxValue
    // Nothing to scroll, or the content has not been measured yet.
    if (maxScroll <= 0 || maxScroll == Int.MAX_VALUE) return

    val vertical = orientation == Orientation.Vertical

    BoxWithConstraints(
        modifier = if (vertical) {
            modifier.fillMaxHeight().width(Thickness)
        } else {
            modifier.fillMaxWidth().height(Thickness)
        }
    ) {
        val density = LocalDensity.current
        val trackLength = if (vertical) constraints.maxHeight else constraints.maxWidth
        val minThumbLength = with(density) { MinThumbLength.roundToPx() }.coerceAtMost(trackLength)

        // The thumb covers the same share of the bar as the view does of the content.
        val thumbLength = (trackLength.toFloat() * trackLength / (trackLength + maxScroll))
            .roundToInt()
            .coerceIn(minThumbLength, trackLength)

        // How far the thumb can move along the bar.
        val travel = (trackLength - thumbLength).coerceAtLeast(1)

        val dragState = rememberDraggableState { delta ->
            scrollState.dispatchRawDelta(delta * maxScroll / travel)
        }

        val thumbLengthDp = with(density) { thumbLength.toDp() }
        Box(
            modifier = Modifier
                .offset {
                    val position = (travel.toFloat() * scrollState.value / maxScroll).roundToInt()
                    if (vertical) IntOffset(0, position) else IntOffset(position, 0)
                }
                .then(
                    if (vertical) Modifier.fillMaxWidth().height(thumbLengthDp)
                    else Modifier.fillMaxHeight().width(thumbLengthDp)
                )
                .draggable(state = dragState, orientation = orientation)
                .padding(ThumbPadding)
                .background(color, RoundedCornerShape(percent = 50))
        )
    }
}
