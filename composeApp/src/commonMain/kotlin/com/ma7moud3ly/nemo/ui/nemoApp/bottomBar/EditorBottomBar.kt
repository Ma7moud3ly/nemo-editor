package com.ma7moud3ly.nemo.ui.nemoApp.bottomBar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.nemo.model.CodeState
import com.ma7moud3ly.nemo.model.EditorSettings
import com.ma7moud3ly.nemo.model.Language
import com.ma7moud3ly.nemo.themes.AppTheme
import com.ma7moud3ly.nemo.themes.EditorThemes
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.bottom_bar_cursor_position
import nemoeditor.composeapp.generated.resources.bottom_bar_encoding
import nemoeditor.composeapp.generated.resources.bottom_bar_font_size
import nemoeditor.composeapp.generated.resources.bottom_bar_read_only
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun EditorBottomBarPreview() {
    AppTheme(theme = EditorThemes.NEMO_LIGHT) {
        EditorBottomBar(
            modifier = Modifier.fillMaxWidth(),
            state = CodeState(language = Language.KOTLIN),
            settings = EditorSettings(readOnly = true)
        )
    }
}

@Composable
internal fun EditorBottomBar(
    modifier: Modifier,
    state: CodeState,
    settings: EditorSettings
) {

    // Metadata
    val totalLines = remember(state.code) {
        state.code.split('\n').size
    }

    val currentLineIndex = remember(state.cursorPosition, state.code) {
        state.code.substring(
            0,
            state.cursorPosition.coerceAtMost(state.code.length)
        ).count { it == '\n' }
    }
    val cursorLine = remember(currentLineIndex) {
        currentLineIndex + 1
    }

    val cursorColumn = remember(state.cursorPosition, state.code) {
        getCursorColumn(state.code, state.cursorPosition)
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(Res.string.bottom_bar_cursor_position, cursorLine, totalLines, cursorColumn),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (settings.readOnlyState.value) {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(Res.string.bottom_bar_read_only),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Text(
                    stringResource(Res.string.bottom_bar_font_size, settings.fontSizeState.value),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    state.language.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    stringResource(Res.string.bottom_bar_encoding),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}


// Helper functions
private fun getCursorColumn(text: String, cursorPosition: Int): Int {
    val textBeforeCursor = text.take(cursorPosition.coerceAtMost(text.length))
    val lastNewline = textBeforeCursor.lastIndexOf('\n')
    return if (lastNewline == -1) cursorPosition + 1 else cursorPosition - lastNewline
}
