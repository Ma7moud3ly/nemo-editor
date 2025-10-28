package io.ma7moud3ly.nemo.ui.nemoApp.sideBar.explorer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ma7moud3ly.nemo.managers.FilesManager
import io.ma7moud3ly.nemo.model.EditorAction
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.NemoFile
import io.ma7moud3ly.nemo.ui.AppTheme
import io.ma7moud3ly.nemo.ui.nemoApp.directoryColor
import io.ma7moud3ly.nemo.ui.nemoApp.iconColor
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.files_explorer_create_new
import nemoeditor.composeapp.generated.resources.files_explorer_empty_folder
import nemoeditor.composeapp.generated.resources.files_explorer_navigate_up
import nemoeditor.composeapp.generated.resources.files_explorer_new_file
import nemoeditor.composeapp.generated.resources.files_explorer_new_folder
import nemoeditor.composeapp.generated.resources.files_explorer_no_folder_opened
import nemoeditor.composeapp.generated.resources.files_explorer_open_folder
import nemoeditor.composeapp.generated.resources.files_explorer_refresh
import nemoeditor.composeapp.generated.resources.files_explorer_root
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun EmptyExplorerContentPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        Surface(modifier = Modifier.fillMaxSize()) {
            ExplorerContent(
                onAction = {},
                filesManager = FilesManager()
            )
        }
    }
}

@Preview
@Composable
private fun ExplorerContentPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        Surface(modifier = Modifier.fillMaxSize()) {
            ExplorerContent(
                onAction = {},
                filesManager = FilesManager(
                    initialRoot = "/home/user/src",
                    initialFiles = listOf(
                        NemoFile(
                            name = "src",
                            isDirectory = true,
                        ),
                        NemoFile(
                            name = "main.py",
                            extension = "py"
                        ),
                        NemoFile(
                            name = "main.kt",
                            extension = "kt"
                        )
                    )
                )
            )
        }
    }
}

@Composable
internal fun ExplorerContent(
    onAction: (EditorAction) -> Unit,
    filesManager: FilesManager
) {
    val rootPath = filesManager.rootDirectoryPath
    val currentPath = filesManager.currentDirectoryPath
    val files = remember { filesManager.currentFiles }
    val isLoading = filesManager.isLoading
    var selectedFile by remember { mutableStateOf<NemoFile?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (rootPath != null) {
            FileExplorerToolbar(
                currentPath = currentPath ?: rootPath,
                canNavigateUp = currentPath != rootPath,
                onNavigateUp = { onAction(EditorAction.NavigateUp) },
                onRefresh = { onAction(EditorAction.Refresh) },
                onNewFile = { onAction(EditorAction.CreateNewFile) },
                onNewFolder = { onAction(EditorAction.CreateNewFolder) }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when {
                isLoading -> LoadingContent()
                rootPath == null -> EmptyExplorerContent(onAction)
                files.isEmpty() -> EmptyFolderContent()
                else -> FileList(
                    files = files,
                    onFileOpen = { file ->
                        if (file.isDirectory) onAction(EditorAction.OpenFolder(file))
                        else onAction(EditorAction.OpenFile(file))
                    },
                    selectedFile = { selectedFile },
                    onFileSelect = { selectedFile = it },
                    onRename = { onAction(EditorAction.RenameFile(selectedFile!!)) },
                    onDelete = { onAction(EditorAction.DeleteFile(selectedFile!!)) },
                    onDetails = { onAction(EditorAction.FileDetails(selectedFile!!)) }
                )
            }
        }
    }


}

@Composable
private fun FileExplorerToolbar(
    currentPath: String,
    canNavigateUp: Boolean,
    onNavigateUp: () -> Unit,
    onRefresh: () -> Unit,
    onNewFile: () -> Unit,
    onNewFolder: () -> Unit
) {
    var showCreateMenu by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentPath.substringAfterLast('/').ifEmpty { stringResource(Res.string.files_explorer_root) },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Create menu
                Box {
                    IconButton(
                        onClick = { showCreateMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            stringResource(Res.string.files_explorer_create_new),
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = showCreateMenu,
                        onDismissRequest = { showCreateMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.files_explorer_new_file)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.InsertDriveFile,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                onNewFile()
                                showCreateMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.files_explorer_new_folder)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = directoryColor
                                )
                            },
                            onClick = {
                                onNewFolder()
                                showCreateMenu = false
                            }
                        )
                    }
                }

                IconButton(
                    onClick = onNavigateUp,
                    enabled = canNavigateUp,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowUpward,
                        stringResource(Res.string.files_explorer_navigate_up),
                        modifier = Modifier.size(18.dp),
                        tint = if (canNavigateUp) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        stringResource(Res.string.files_explorer_refresh),
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileList(
    files: List<NemoFile>,
    selectedFile: () -> NemoFile?,
    onFileSelect: (NemoFile) -> Unit,
    onFileOpen: (NemoFile) -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDetails: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(files, key = { it.path + it.name }) { file ->
            FileListItem(
                file = file,
                isSelected = selectedFile()?.path == file.path,
                onSelect = { onFileSelect(file) },
                onOpen = { onFileOpen(file) },
                onRename = onRename,
                onDelete = onDelete,
                onDetails = onDetails
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileListItem(
    file: NemoFile,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDetails: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.buttons.isSecondaryPressed) {
                                onSelect()
                                showMenu = true
                            }
                        }
                    }
                }
                .combinedClickable(
                    onClick = {
                        onSelect()
                        onOpen()
                    },
                    onLongClick = {
                        onSelect()
                        showMenu = true
                    }
                ),
            color = if (isSelected)
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
            else
                Color.Transparent,
            shape = RoundedCornerShape(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (file.isDirectory) Icons.Default.Folder
                    else Icons.AutoMirrored.Default.InsertDriveFile,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = file.iconColor()
                )

                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (file.isDirectory) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        FileContextMenu(
            file = file,
            onOpen = onOpen,
            showMenu = { showMenu },
            onDismiss = { showMenu = false },
            onRename = onRename,
            onDelete = onDelete,
            onDetails = onDetails
        )
    }
}


@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun EmptyExplorerContent(onAction: (EditorAction) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            stringResource(Res.string.files_explorer_no_folder_opened),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Button(
            onClick = { onAction(EditorAction.PickFolder) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.FolderOpen, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.files_explorer_open_folder))
        }
    }
}

@Composable
private fun EmptyFolderContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.FolderOpen,
                null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            Text(
                stringResource(Res.string.files_explorer_empty_folder),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
