package com.ma7moud3ly.nemo.ui.nemoApp.sideBar.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.nemo.model.NemoFile
import com.ma7moud3ly.nemo.themes.AppTheme
import com.ma7moud3ly.nemo.themes.EditorThemes
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.file_context_menu_delete
import nemoeditor.composeapp.generated.resources.file_context_menu_delete_description
import nemoeditor.composeapp.generated.resources.file_context_menu_details
import nemoeditor.composeapp.generated.resources.file_context_menu_details_description
import nemoeditor.composeapp.generated.resources.file_context_menu_open
import nemoeditor.composeapp.generated.resources.file_context_menu_open_file_description
import nemoeditor.composeapp.generated.resources.file_context_menu_open_folder_description
import nemoeditor.composeapp.generated.resources.file_context_menu_rename
import nemoeditor.composeapp.generated.resources.file_context_menu_rename_file_description
import nemoeditor.composeapp.generated.resources.file_context_menu_rename_folder_description
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun FileContextMenuPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        FileContextMenu(
            showMenu = { true },
            file = NemoFile(
                name = "MainActivity.kt",
                extension = "kt",
                isDirectory = false
            ),
            onOpen = {},
            onDismiss = {},
            onRename = {},
            onDelete = {},
            onDetails = {}
        )
    }
}

@Preview
@Composable
private fun FolderContextMenuPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        FileContextMenu(
            showMenu = { true },
            file = NemoFile(
                name = "components",
                isDirectory = true
            ),
            onOpen = {},
            onDismiss = {},
            onRename = {},
            onDelete = {},
            onDetails = {}
        )
    }
}

@Composable
internal fun FileContextMenu(
    showMenu: () -> Boolean,
    file: NemoFile,
    onOpen: () -> Unit,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDetails: () -> Unit
) {
    DropdownMenu(
        expanded = showMenu(),
        onDismissRequest = onDismiss
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            // Menu items
            ContextMenuItem(
                icon = if (file.isDirectory) Icons.Default.Directions
                else Icons.Default.FileOpen,
                label = stringResource(Res.string.file_context_menu_open),
                description = stringResource(if (file.isDirectory) Res.string.file_context_menu_open_folder_description else Res.string.file_context_menu_open_file_description),
                onClick = {
                    onOpen()
                    onDismiss()
                }
            )
            ContextMenuItem(
                icon = Icons.Default.Edit,
                label = stringResource(Res.string.file_context_menu_rename),
                description = stringResource(
                    if (file.isDirectory) Res.string.file_context_menu_rename_folder_description
                    else Res.string.file_context_menu_rename_file_description
                ),
                onClick = {
                    onRename()
                    onDismiss()
                }
            )

            ContextMenuItem(
                icon = Icons.Default.Info,
                label = stringResource(Res.string.file_context_menu_details),
                description = stringResource(Res.string.file_context_menu_details_description),
                onClick = {
                    onDetails()
                    onDismiss()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            ContextMenuItem(
                icon = Icons.Default.Delete,
                label = stringResource(Res.string.file_context_menu_delete),
                description = stringResource(Res.string.file_context_menu_delete_description),
                onClick = {
                    onDelete()
                    onDismiss()
                },
                isDestructive = true
            )
        }
    }
}


@Composable
private fun ContextMenuItem(
    icon: ImageVector,
    label: String,
    description: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isDestructive)
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        else
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isDestructive)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onPrimary
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
