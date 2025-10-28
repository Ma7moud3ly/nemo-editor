package io.ma7moud3ly.nemo.ui.nemoApp.sideBar.explorer

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
import io.ma7moud3ly.nemo.ui.AppTheme
import io.ma7moud3ly.nemo.model.EditorThemes
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.create_item_dialog_cancel
import nemoeditor.composeapp.generated.resources.create_item_dialog_create
import nemoeditor.composeapp.generated.resources.create_item_dialog_invalid_chars
import nemoeditor.composeapp.generated.resources.create_item_dialog_name_empty
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun CreateItemDialogPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        CreateItemDialog(
            title = "New File",
            label = "File name",
            onDismiss = {},
            onConfirm = {}
        )
    }
}

@Composable
internal fun CreateItemDialog(
    title: String,
    label: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val itemEmpty = stringResource(Res.string.create_item_dialog_name_empty)
    val invalidChars = stringResource(Res.string.create_item_dialog_invalid_chars)

    AlertDialog(
        titleContentColor = MaterialTheme.colorScheme.secondary,
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        error = when {
                            it.isEmpty() -> itemEmpty
                            it.contains('/') || it.contains('\\') -> invalidChars
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
                            text = label,
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
                onClick = { onConfirm(name) },
                enabled = name.isNotEmpty() && error == null
            ) {
                Text(stringResource(Res.string.create_item_dialog_create))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(stringResource(Res.string.create_item_dialog_cancel))
            }
        }
    )
}

