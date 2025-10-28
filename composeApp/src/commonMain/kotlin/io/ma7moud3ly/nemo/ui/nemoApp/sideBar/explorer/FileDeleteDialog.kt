package io.ma7moud3ly.nemo.ui.nemoApp.sideBar.explorer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.ma7moud3ly.nemo.model.NemoFile
import io.ma7moud3ly.nemo.ui.AppTheme
import io.ma7moud3ly.nemo.model.EditorThemes
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.delete_dialog_cancel
import nemoeditor.composeapp.generated.resources.delete_dialog_delete
import nemoeditor.composeapp.generated.resources.delete_dialog_message
import nemoeditor.composeapp.generated.resources.delete_dialog_title_file
import nemoeditor.composeapp.generated.resources.delete_dialog_title_folder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun DeleteConfirmationPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        DeleteConfirmationDialog(
            file = NemoFile(name = "ExampleFile.kt"),
            onDismiss = {},
            onConfirm = {}
        )
    }
}

@Composable
internal fun DeleteConfirmationDialog(
    file: NemoFile,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        titleContentColor = MaterialTheme.colorScheme.secondary,
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Delete,
                null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text(stringResource(if (file.isDirectory) Res.string.delete_dialog_title_folder else Res.string.delete_dialog_title_file)) },
        text = {
            Text(
                stringResource(Res.string.delete_dialog_message, file.name),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(Res.string.delete_dialog_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = Color.Transparent
                )
            ) {
                Text(stringResource(Res.string.delete_dialog_cancel))
            }
        }
    )
}