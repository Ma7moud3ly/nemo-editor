package io.ma7moud3ly.nemo.ui.nemoApp.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.ui.AppTheme
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.ui.nemoApp.MyDialog
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.settings_dialog_auto_indent
import nemoeditor.composeapp.generated.resources.settings_dialog_autocomplete
import nemoeditor.composeapp.generated.resources.settings_dialog_decrease
import nemoeditor.composeapp.generated.resources.settings_dialog_display
import nemoeditor.composeapp.generated.resources.settings_dialog_editor
import nemoeditor.composeapp.generated.resources.settings_dialog_font_size
import nemoeditor.composeapp.generated.resources.settings_dialog_increase
import nemoeditor.composeapp.generated.resources.settings_dialog_show_line_numbers
import nemoeditor.composeapp.generated.resources.settings_dialog_tab_size
import nemoeditor.composeapp.generated.resources.settings_dialog_title
import nemoeditor.composeapp.generated.resources.settings_dialog_use_tabs
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun SettingsDialogPreview() {
    AppTheme(theme = EditorThemes.NEMO_LIGHT) {
        SettingsDialog(
            settings = EditorSettings(),
            onDismiss = {}
        )
    }
}

@Composable
internal fun SettingsDialog(
    settings: EditorSettings,
    onDismiss: () -> Unit
) {
    MyDialog(
        onDismiss = onDismiss,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            stringResource(Res.string.settings_dialog_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(Modifier.height(16.dp))

        SettingSection(stringResource(Res.string.settings_dialog_display)) {
            val fontSize by remember { settings.fontSizeState }
            NumberSettingRow(
                label = stringResource(Res.string.settings_dialog_font_size),
                value = fontSize,
                onIncrement = { settings.zoomIn(1) },
                onDecrement = { settings.zoomOut(1) }
            )

            var showLineNumbers by remember { settings.showLineNumbersState }
            SwitchSettingRow(
                label = stringResource(Res.string.settings_dialog_show_line_numbers),
                checked = showLineNumbers,
                onCheckedChange = { showLineNumbers = it }
            )
        }


        SettingSection(stringResource(Res.string.settings_dialog_editor)) {
            val tabSize by remember { settings.tabSizeState }
            NumberSettingRow(
                label = stringResource(Res.string.settings_dialog_tab_size),
                value = tabSize,
                onIncrement = { settings.increaseTabSize() },
                onDecrement = { settings.decreaseTabSize() }
            )

            var useTabs by remember { settings.useTabsState }
            SwitchSettingRow(
                label = stringResource(Res.string.settings_dialog_use_tabs),
                checked = useTabs,
                onCheckedChange = { useTabs = it }
            )

            var enableAutoIndent by remember { settings.enableAutoIndentState }
            SwitchSettingRow(
                label = stringResource(Res.string.settings_dialog_auto_indent),
                checked = enableAutoIndent,
                onCheckedChange = { enableAutoIndent = it }
            )

            var enableAutocomplete by remember { settings.enableAutocompleteState }
            SwitchSettingRow(
                label = stringResource(Res.string.settings_dialog_autocomplete),
                checked = enableAutocomplete,
                onCheckedChange = { enableAutocomplete = it }
            )
        }
    }
}

@Composable
private fun NumberSettingRow(
    label: String,
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            IconButton(onClick = onDecrement) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(Res.string.settings_dialog_decrease, label),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = "$value",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            IconButton(onClick = onIncrement) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = stringResource(Res.string.settings_dialog_increase, label),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun SwitchSettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            ),
            modifier = modifier
        )
    }
}

@Composable
private fun SettingSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}
