package io.ma7moud3ly.nemo.feature.bottomBar

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.ui.AppTheme
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.shared.resources.Res
import io.ma7moud3ly.nemo.shared.resources.bottom_bar_cursor_position
import io.ma7moud3ly.nemo.shared.resources.bottom_bar_encoding
import io.ma7moud3ly.nemo.shared.resources.bottom_bar_font_size
import io.ma7moud3ly.nemo.shared.resources.bottom_bar_read_only
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun NemoBottomBarPreview() {
    AppTheme(theme = EditorThemes.NEMO_LIGHT) {
        NemoBottomBar(
            modifier = Modifier.fillMaxWidth(),
            state = CodeState(language = Language.KOTLIN),
            settings = EditorSettings(readOnly = true)
        )
    }
}

@Composable
internal fun NemoBottomBar(
    modifier: Modifier,
    state: CodeState,
    settings: EditorSettings,
    background: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
) {
    Surface(
        color = background,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(
                    Res.string.bottom_bar_cursor_position,
                    state.currentLine,
                    state.totalLines,
                    state.currentColumn
                ),
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
                    stringResource(
                        Res.string.bottom_bar_font_size,
                        settings.fontSizeState.value
                    ),
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
