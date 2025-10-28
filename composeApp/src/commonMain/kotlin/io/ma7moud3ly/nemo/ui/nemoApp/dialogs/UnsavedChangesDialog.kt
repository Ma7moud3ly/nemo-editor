package io.ma7moud3ly.nemo.ui.nemoApp.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.ma7moud3ly.nemo.platform.LocalPlatform
import io.ma7moud3ly.nemo.platform.isWasmJs
import io.ma7moud3ly.nemo.ui.AppTheme
import io.ma7moud3ly.nemo.model.EditorThemes
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.unsaved_changes_dialog_cancel
import nemoeditor.composeapp.generated.resources.unsaved_changes_dialog_discard
import nemoeditor.composeapp.generated.resources.unsaved_changes_dialog_export
import nemoeditor.composeapp.generated.resources.unsaved_changes_dialog_message
import nemoeditor.composeapp.generated.resources.unsaved_changes_dialog_save
import nemoeditor.composeapp.generated.resources.unsaved_changes_dialog_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun UnsavedChangesDialogPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        UnsavedChangesDialog(
            onSave = {},
            onExport = {},
            onDiscard = {},
            onCancel = {}
        )
    }
}

@Composable
internal fun UnsavedChangesDialog(
    onSave: () -> Unit,
    onExport: () -> Unit,
    onDiscard: () -> Unit,
    onCancel: () -> Unit
) {
    val platform = LocalPlatform.current
    AlertDialog(
        onDismissRequest = onCancel,
        titleContentColor = MaterialTheme.colorScheme.secondary,
        textContentColor = MaterialTheme.colorScheme.tertiary,
        title = {
            Text(stringResource(Res.string.unsaved_changes_dialog_title))
        },
        text = {
            Text(stringResource(Res.string.unsaved_changes_dialog_message))
        },
        confirmButton = {
            if (platform.isWasmJs) TextButton(onClick = onExport) {
                Text(
                    text = stringResource(Res.string.unsaved_changes_dialog_export),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else TextButton(onClick = onSave) {
                Text(
                    text = stringResource(Res.string.unsaved_changes_dialog_save),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDiscard) {
                    Text(
                        text = stringResource(Res.string.unsaved_changes_dialog_discard),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                TextButton(onClick = onCancel) {
                    Text(
                        text = stringResource(Res.string.unsaved_changes_dialog_cancel),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    )
}
