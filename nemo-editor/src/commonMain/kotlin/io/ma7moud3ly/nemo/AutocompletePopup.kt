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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
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


/**
 * Autocomplete popup with keyboard navigation
 *
 * @param state The code editor state
 * @param settings The editor settings
 * @param scrollOffset The current vertical scroll offset of the editor
 * @param autocompleteState State holder for autocomplete
 */
@Composable
fun AutocompletePopup(
    state: CodeState,
    settings: EditorSettings,
    scrollOffset: Int = 0,
    autocompleteState: AutocompleteState
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
    val fontSize = settings.fontSizeState.value
    val lineHeight = fontSize * 1.5f
    val coroutineScope = rememberCoroutineScope()

    // Calculate line numbers width using autocomplete state
    val lineNumbersWidth = remember(
        settings.showLineNumbersState.value,
        state.totalLines
    ) {
        autocompleteState.calculateLineNumbersWidth(
            showLineNumbers = settings.showLineNumbersState.value,
            totalLines = state.totalLines
        )
    }

    // Trigger autocomplete when text or cursor changes (with debounce)
    LaunchedEffect(state.code, state.cursorPosition) {
        // Small delay to debounce rapid typing
        delay(100)

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

    val listState = rememberLazyListState()
    val density = LocalDensity.current

    // Calculate cursor position
    val currentLineIndex = state.currentLine - 1

    // Calculate position of the NEXT line (below current line)
    val currentLinePositionPx = (currentLineIndex * lineHeight).toInt()
    val nextLinePositionPx = currentLinePositionPx + lineHeight.toInt()
    val visiblePositionPx = nextLinePositionPx - scrollOffset

    val verticalOffsetDp = with(density) {
        visiblePositionPx.toDp() + 40.dp
    }

    val horizontalOffsetDp = with(density) {
        lineNumbersWidth.toDp() + 8.dp
    }

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
            .offset {
                IntOffset(
                    x = with(density) { horizontalOffsetDp.roundToPx() },
                    y = with(density) { verticalOffsetDp.roundToPx() }
                )
            }
    ) {
        Surface(
            modifier = Modifier
                .width(320.dp)
                .heightIn(max = 250.dp),
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