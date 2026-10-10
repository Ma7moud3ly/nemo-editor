package io.ma7moud3ly.nemo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.lsp.completion.AutocompleteState
import io.ma7moud3ly.nemo.lsp.completion.CompletionProviderFactory
import io.ma7moud3ly.nemo.lsp.completion.getColor
import io.ma7moud3ly.nemo.lsp.completion.getIcon
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.CompletionItem
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds


/**
 * Autocomplete popup, placed against the caret.
 *
 * The popup sits below the caret line, or above it when there is not enough
 * room below, and is kept inside the editor viewport either way.
 *
 * @param state The code editor state
 * @param settings The editor settings
 * @param autocompleteState State holder for autocomplete
 * @param caretRect Caret bounds in pixels, relative to the top-left of the code
 *   area and already adjusted for scrolling. `null` before the code has been
 *   laid out.
 * @param viewportSize Size of the visible code area, in pixels.
 */
@Composable
fun AutocompletePopup(
    state: CodeState,
    settings: EditorSettings,
    autocompleteState: AutocompleteState,
    caretRect: () -> Rect?,
    viewportSize: () -> IntSize
) {
    // Only show if autocomplete is enabled and not in read-only mode
    if (!settings.enableAutocompleteState.value ||
        settings.readOnlyState.value
    ) return

    // Create language-specific completion provider
    val completionProvider = remember(state.language) {
        CompletionProviderFactory.getProvider(state.language)
    }

    val theme by remember { settings.themeState }
    val coroutineScope = rememberCoroutineScope()

    // The text the last lookup ran for. It starts as the text the editor
    // opened with, so nothing is suggested until the user edits it.
    var lastCode by remember(state) { mutableStateOf(state.code) }

    // Trigger autocomplete when the text changes (with debounce)
    LaunchedEffect(state.code, state.cursorPosition) {
        // Small delay to debounce rapid typing
        delay(100.milliseconds)

        // The caret moved without an edit, or the editor has just opened.
        if (state.code == lastCode) {
            autocompleteState.hide()
            return@LaunchedEffect
        }
        lastCode = state.code

        val cursorPos = state.cursorPosition
        val textBeforeCursor = state.code.take(cursorPos)
        val lastChar = textBeforeCursor.lastOrNull()

        // Show autocomplete if typing letters, digits, or after dot
        if (lastChar != null && (lastChar.isLetterOrDigit() || lastChar == '.')) {
            coroutineScope.launch {
                val provider = completionProvider ?: return@launch
                val completions = provider.provideCompletions(state.code, cursorPos)
                autocompleteState.show(completions)
            }
        } else {
            autocompleteState.hide()
        }
    }

    // Don't render if not showing
    if (!autocompleteState.isVisible) return

    val caret = caretRect() ?: return
    val viewport = viewportSize()
    if (viewport.width == 0 || viewport.height == 0) return

    val listState = rememberLazyListState()
    val density = LocalDensity.current

    // Measured on the first frame; until then assume the tallest the popup can
    // get, so the first placement errs towards flipping above rather than
    // overlapping the line being typed.
    var popupHeight by remember { mutableIntStateOf(0) }

    val gap = with(density) { POPUP_GAP.toPx() }
    val width = with(density) { POPUP_WIDTH.toPx() }
    val height = if (popupHeight > 0) {
        popupHeight.toFloat()
    } else {
        with(density) { POPUP_MAX_HEIGHT.toPx() }
    }

    // Prefer below the caret line. Flip above only when the popup does not fit
    // below and does fit above, so it never covers what is being typed.
    val below = caret.bottom + gap
    val above = caret.top - gap - height
    val y = if (below + height <= viewport.height || above < 0f) below else above

    val offsetX = caret.left.coerceIn(0f, (viewport.width - width).coerceAtLeast(0f))
    val offsetY = y.coerceIn(0f, (viewport.height - height).coerceAtLeast(0f))

    // Auto-scroll to selected item
    LaunchedEffect(autocompleteState.selectedIndex) {
        if (autocompleteState.selectedIndex in autocompleteState.items.indices) {
            coroutineScope.launch {
                listState.animateScrollToItem(autocompleteState.selectedIndex)
            }
        }
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .onSizeChanged { popupHeight = it.height }
    ) {
        Surface(
            modifier = Modifier
                .width(POPUP_WIDTH)
                .heightIn(max = POPUP_MAX_HEIGHT),
            shape = RoundedCornerShape(8.dp),
            color = Color(theme.background),
            shadowElevation = 8.dp,
            tonalElevation = 2.dp,
            border = BorderStroke(
                1.dp,
                Color(theme.lineNumber).copy(alpha = 0.3f)
            )
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.padding(2.dp)
            ) {
                itemsIndexed(autocompleteState.items) { index, item ->
                    AutocompleteItem(
                        item = item,
                        isSelected = index == autocompleteState.selectedIndex,
                        theme = theme,
                        onClick = {
                            state.insertCompletion(item.insertText)
                            autocompleteState.markCompletionInserted()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AutocompleteItem(
    item: CompletionItem,
    isSelected: Boolean,
    theme: EditorTheme,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected)
                    Color(theme.selection).copy(alpha = 0.4f)
                else
                    Color.Transparent
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.kind.getIcon(),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = item.kind.getColor(theme)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp
                ),
                color = Color(theme.foreground)
            )

            item.detail?.let { detail ->
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    ),
                    color = Color(theme.lineNumber),
                    maxLines = 1
                )
            }
        }

        Text(
            text = item.kind.name.lowercase().take(3),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = Color(theme.lineNumber).copy(alpha = 0.7f)
        )
    }
}

private val POPUP_WIDTH = 320.dp
private val POPUP_MAX_HEIGHT = 250.dp

/** Space left between the caret line and the popup. */
private val POPUP_GAP = 4.dp
