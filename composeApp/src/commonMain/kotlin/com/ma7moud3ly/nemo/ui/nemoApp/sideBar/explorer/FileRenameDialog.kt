package com.ma7moud3ly.nemo.ui.nemoApp.sideBar.explorer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ma7moud3ly.nemo.model.NemoFile
import com.ma7moud3ly.nemo.themes.AppTheme
import com.ma7moud3ly.nemo.themes.EditorThemes
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.rename_dialog_cancel
import nemoeditor.composeapp.generated.resources.rename_dialog_invalid_chars
import nemoeditor.composeapp.generated.resources.rename_dialog_name_empty
import nemoeditor.composeapp.generated.resources.rename_dialog_name_unchanged
import nemoeditor.composeapp.generated.resources.rename_dialog_new_name
import nemoeditor.composeapp.generated.resources.rename_dialog_rename
import nemoeditor.composeapp.generated.resources.rename_dialog_title_file
import nemoeditor.composeapp.generated.resources.rename_dialog_title_folder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun RenameDialogPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        RenameDialog(
            file = NemoFile(name = "ExampleFile.kt"),
            onDismiss = {},
            onConfirm = {}
        )
    }
}

@Composable
internal fun RenameDialog(
    file: NemoFile,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(file.name) }
    var error by remember { mutableStateOf<String?>(null) }
    val nameEmpty = stringResource(Res.string.rename_dialog_name_empty)
    val invalidChars = stringResource(Res.string.rename_dialog_invalid_chars)
    val nameUnchanged = stringResource(Res.string.rename_dialog_name_unchanged)

    AlertDialog(
        titleContentColor = MaterialTheme.colorScheme.secondary,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(if (file.isDirectory) Res.string.rename_dialog_title_folder else Res.string.rename_dialog_title_file)) },
        text = {
            Column {
                OutlinedTextField(
                    value = newName,
                    onValueChange = {
                        newName = it
                        error = when {
                            it.isEmpty() -> nameEmpty
                            it.contains('/') || it.contains('\\') -> invalidChars
                            it == file.name -> nameUnchanged
                            else -> null
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
                        cursorColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    label = {
                        Text(
                            text = stringResource(Res.string.rename_dialog_new_name),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    isError = error != null,
                    supportingText = error?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newName) },
                enabled = newName.isNotEmpty() && error == null && newName != file.name
            ) {
                Text(stringResource(Res.string.rename_dialog_rename))
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
                Text(stringResource(Res.string.rename_dialog_cancel))
            }
        }
    )
}
