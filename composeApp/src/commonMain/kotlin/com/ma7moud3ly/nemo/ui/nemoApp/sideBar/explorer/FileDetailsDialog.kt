package com.ma7moud3ly.nemo.ui.nemoApp.sideBar.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ma7moud3ly.nemo.managers.FilesManager.Companion.getFileTypeLabel
import com.ma7moud3ly.nemo.model.NemoFile
import com.ma7moud3ly.nemo.model.formatLastModified
import com.ma7moud3ly.nemo.model.formatSize
import com.ma7moud3ly.nemo.themes.AppTheme
import com.ma7moud3ly.nemo.themes.EditorThemes
import com.ma7moud3ly.nemo.ui.nemoApp.iconColor
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.file_details_dialog_close
import nemoeditor.composeapp.generated.resources.file_details_dialog_extension
import nemoeditor.composeapp.generated.resources.file_details_dialog_folder
import nemoeditor.composeapp.generated.resources.file_details_dialog_general
import nemoeditor.composeapp.generated.resources.file_details_dialog_location
import nemoeditor.composeapp.generated.resources.file_details_dialog_modified
import nemoeditor.composeapp.generated.resources.file_details_dialog_name
import nemoeditor.composeapp.generated.resources.file_details_dialog_path
import nemoeditor.composeapp.generated.resources.file_details_dialog_size
import nemoeditor.composeapp.generated.resources.file_details_dialog_statistics
import nemoeditor.composeapp.generated.resources.file_details_dialog_type
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun FileDetailsDialogPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        FileDetailsDialog(
            file = NemoFile(
                name = "MainActivity.kt",
                path = "/home/user/projects/MyApp/src/main/kotlin/MainActivity.kt",
                extension = "kt",
                isDirectory = false,
                size = 4321,
                lastModified = Clock.System.now().toEpochMilliseconds() - (2 * 60 * 60 * 1000)
            ),
            onDismiss = {}
        )
    }
}

@Composable
internal fun FileDetailsDialog(
    file: NemoFile,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        modifier = Modifier.width(420.dp).heightIn(max = 600.dp),
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(file.iconColor().copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (file.isDirectory) Icons.Default.Folder
                        else Icons.AutoMirrored.Default.InsertDriveFile,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = file.iconColor()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (file.isDirectory) stringResource(Res.string.file_details_dialog_folder) else file.getFileTypeLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // General Section
                DetailSection(
                    title = stringResource(Res.string.file_details_dialog_general),
                    items = buildList {
                        add(DetailItem(Icons.AutoMirrored.Default.Label, stringResource(Res.string.file_details_dialog_name), file.name))
                        add(
                            DetailItem(
                                if (file.isDirectory) Icons.Default.Folder
                                else Icons.AutoMirrored.Default.InsertDriveFile,
                                stringResource(Res.string.file_details_dialog_type),
                                if (file.isDirectory) stringResource(Res.string.file_details_dialog_folder) else file.getFileTypeLabel()
                            )
                        )
                        if (!file.isDirectory) {
                            add(
                                DetailItem(
                                    Icons.Default.Extension,
                                    stringResource(Res.string.file_details_dialog_extension),
                                    ".${file.extension}"
                                )
                            )
                        }
                    }
                )

                // Location Section
                DetailSection(
                    title = stringResource(Res.string.file_details_dialog_location),
                    items = listOf(
                        DetailItem(Icons.Default.FolderOpen, stringResource(Res.string.file_details_dialog_path), file.path)
                    )
                )

                // Statistics Section
                DetailSection(
                    title = stringResource(Res.string.file_details_dialog_statistics),
                    items = buildList {
                        if (!file.isDirectory && file.size > 0) {
                            add(DetailItem(Icons.Default.Storage, stringResource(Res.string.file_details_dialog_size), file.formatSize()))
                        }
                        if (file.lastModified > 0) {
                            add(
                                DetailItem(
                                    Icons.Default.Schedule,
                                    stringResource(Res.string.file_details_dialog_modified),
                                    file.formatLastModified()
                                )
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = onDismiss,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(stringResource(Res.string.file_details_dialog_close))
            }
        }
    )
}

@Composable
private fun DetailSection(
    title: String,
    items: List<DetailItem>
) {
    if (items.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items.forEachIndexed { index, item ->
                    DetailRow(
                        icon = item.icon,
                        label = item.label,
                        value = item.value
                    )

                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private data class DetailItem(
    val icon: ImageVector,
    val label: String,
    val value: String
)
