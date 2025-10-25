package com.ma7moud3ly.nemo.ui.nemoApp.topBar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SaveAs
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.nemo.platform.LocalPlatform
import com.ma7moud3ly.nemo.platform.isWasmJs
import com.ma7moud3ly.nemo.model.CodeState
import com.ma7moud3ly.nemo.model.EditorAction
import com.ma7moud3ly.nemo.model.EditorTab
import com.ma7moud3ly.nemo.model.NemoFile
import com.ma7moud3ly.nemo.platform.exists
import com.ma7moud3ly.nemo.platform.isJvm
import com.ma7moud3ly.nemo.themes.EditorThemes
import com.ma7moud3ly.nemo.themes.AppTheme
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.top_bar_help_about
import nemoeditor.composeapp.generated.resources.top_bar_edit
import nemoeditor.composeapp.generated.resources.top_bar_edit_find_replace
import nemoeditor.composeapp.generated.resources.top_bar_edit_format_code
import nemoeditor.composeapp.generated.resources.top_bar_edit_redo
import nemoeditor.composeapp.generated.resources.top_bar_edit_undo
import nemoeditor.composeapp.generated.resources.top_bar_file
import nemoeditor.composeapp.generated.resources.top_bar_file_delete
import nemoeditor.composeapp.generated.resources.top_bar_file_details
import nemoeditor.composeapp.generated.resources.top_bar_file_export
import nemoeditor.composeapp.generated.resources.top_bar_file_new
import nemoeditor.composeapp.generated.resources.top_bar_file_open
import nemoeditor.composeapp.generated.resources.top_bar_file_rename
import nemoeditor.composeapp.generated.resources.top_bar_file_save
import nemoeditor.composeapp.generated.resources.top_bar_file_save_as
import nemoeditor.composeapp.generated.resources.top_bar_find
import nemoeditor.composeapp.generated.resources.top_bar_help
import nemoeditor.composeapp.generated.resources.top_bar_help_documentation
import nemoeditor.composeapp.generated.resources.top_bar_help_keyboard_shortcuts
import nemoeditor.composeapp.generated.resources.top_bar_view
import nemoeditor.composeapp.generated.resources.top_bar_view_appearance
import nemoeditor.composeapp.generated.resources.top_bar_view_settings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun EditorTopBarPreview() {
    AppTheme(theme = EditorThemes.NEMO_LIGHT) {
        EditorTopBar(
            modifier = Modifier.fillMaxWidth(),
            animatedLogo = true,
            onAction = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditorTopBar(
    tab: EditorTab? = null,
    modifier: Modifier = Modifier,
    animatedLogo: Boolean,
    onAction: (EditorAction) -> Unit
) {
    val codeState = tab?.codeState

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Nemo Logo
            if (animatedLogo) {
                NemoAnimatedLogo(
                    onClick = { onAction(EditorAction.ToggleLogo) }
                )
            } else {
                NemoStaticLogo(
                    onClick = { onAction(EditorAction.ToggleLogo) }
                )
            }
            Spacer(modifier = Modifier.width(4.dp))

            /*Text(
                "Nemo",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )*/

            // File Menu
            MenuFile(
                tab = tab,
                onAction = onAction
            )

            // Edit menu
            codeState?.let {
                MenuEdit(
                    state = it,
                    onAction = onAction
                )
            }

            // View menu
            MenuView(onAction = onAction)

            // Help menu
            MenuHelp(onAction = onAction)

            Spacer(modifier = Modifier.weight(1f))

            Spacer(Modifier.weight(1f))

            // Quick Actions
            codeState?.let {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { onAction(EditorAction.ToggleFind) }) {
                        Icon(Icons.Default.Search, stringResource(Res.string.top_bar_find))
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBarMenuButton(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        fontWeight = FontWeight.Normal
    )
}

@Composable
private fun MenuFile(
    tab: EditorTab?,
    onAction: (EditorAction) -> Unit
) {
    val platform = LocalPlatform.current
    val hasCode = tab?.codeState != null
    val hasFile = tab?.file?.exists() == true
    Box {
        // File menu
        var showFileMenu by remember { mutableStateOf(false) }
        TopBarMenuButton(
            text = stringResource(Res.string.top_bar_file),
            onClick = { showFileMenu = true }
        )
        DropdownMenu(
            expanded = showFileMenu,
            onDismissRequest = { showFileMenu = false },
            offset = DpOffset(0.dp, 4.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MenuItemWithIcon(
                text = stringResource(Res.string.top_bar_file_new),
                icon = Icons.Default.Add,
                onClick = {
                    onAction(EditorAction.NewUntitledFile)
                    showFileMenu = false
                }
            )
            MenuItemWithIcon(
                text = stringResource(Res.string.top_bar_file_open),
                icon = Icons.Default.FolderOpen,
                onClick = {
                    onAction(EditorAction.PickFile)
                    showFileMenu = false
                }
            )
            if (hasCode && platform.isWasmJs) {
                MenuItemWithIcon(
                    text = stringResource(Res.string.top_bar_file_export),
                    icon = Icons.Default.Download,
                    onClick = {
                        onAction(EditorAction.Export)
                        showFileMenu = false
                    }
                )
            } else if (hasCode) {
                HorizontalDivider(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                MenuItemWithIcon(
                    text = stringResource(Res.string.top_bar_file_save),
                    icon = Icons.Default.Save,
                    onClick = {
                        onAction(EditorAction.Save)
                        showFileMenu = false
                    }
                )
                MenuItemWithIcon(
                    text = stringResource(Res.string.top_bar_file_save_as),
                    icon = Icons.Default.SaveAs,
                    onClick = {
                        onAction(EditorAction.SaveAs)
                        showFileMenu = false
                    }
                )
                if (hasFile) {
                    val file: NemoFile = tab.file
                    HorizontalDivider()
                    if (platform.isJvm) MenuItemWithIcon(
                        text = stringResource(Res.string.top_bar_file_rename),
                        icon = Icons.Default.DriveFileRenameOutline,
                        onClick = {
                            onAction(EditorAction.RenameFile(file))
                            showFileMenu = false
                        }
                    )
                    MenuItemWithIcon(
                        text = stringResource(Res.string.top_bar_file_delete),
                        icon = Icons.Default.Delete,
                        onClick = {
                            onAction(EditorAction.DeleteFile(file))
                            showFileMenu = false
                        }
                    )
                    MenuItemWithIcon(
                        text = stringResource(Res.string.top_bar_file_details),
                        icon = Icons.Default.Info,
                        onClick = {
                            onAction(EditorAction.FileDetails(file))
                            showFileMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuEdit(
    state: CodeState,
    onAction: (EditorAction) -> Unit
) {
    Box {
        var showEditMenu by remember { mutableStateOf(false) }
        TopBarMenuButton(
            text = stringResource(Res.string.top_bar_edit),
            onClick = { showEditMenu = true }
        )
        DropdownMenu(
            expanded = showEditMenu,
            onDismissRequest = { showEditMenu = false },
            offset = DpOffset(0.dp, 4.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MenuItemWithIcon(
                text = stringResource(Res.string.top_bar_edit_undo),
                icon = Icons.AutoMirrored.Default.ArrowBack,
                enabled = state.canUndo(),
                onClick = {
                    onAction(EditorAction.Undo)
                    showEditMenu = false
                }
            )
            MenuItemWithIcon(
                text = stringResource(Res.string.top_bar_edit_redo),
                icon = Icons.AutoMirrored.Default.ArrowForward,
                enabled = state.canRedo(),
                onClick = {
                    onAction(EditorAction.Redo)
                    showEditMenu = false
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
            MenuItemWithIcon(
                text = stringResource(Res.string.top_bar_edit_find_replace),
                icon = Icons.Default.Search,
                onClick = {
                    onAction(EditorAction.ToggleFind)
                    showEditMenu = false
                }
            )
            MenuItemWithIcon(
                text = stringResource(Res.string.top_bar_edit_format_code),
                icon = Icons.Default.Build,
                onClick = {
                    onAction(EditorAction.Format)
                    showEditMenu = false
                }
            )
        }
    }
}

@Composable
private fun MenuView(
    onAction: (EditorAction) -> Unit
) {
    Box {
        var showViewMenu by remember { mutableStateOf(false) }
        TopBarMenuButton(
            text = stringResource(Res.string.top_bar_view),
            onClick = { showViewMenu = true }
        )
        DropdownMenu(
            expanded = showViewMenu,
            onDismissRequest = { showViewMenu = false },
            offset = DpOffset(0.dp, 4.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MenuItemWithIcon(
                text = stringResource(Res.string.top_bar_view_appearance),
                icon = Icons.Default.Palette,
                onClick = {
                    onAction(EditorAction.ToggleTheme)
                    showViewMenu = false
                }
            )
            MenuItemWithIcon(
                text = stringResource(Res.string.top_bar_view_settings),
                icon = Icons.Default.Settings,
                onClick = {
                    onAction(EditorAction.OpenSettings)
                    showViewMenu = false
                }
            )
        }
    }
}

@Composable
private fun MenuHelp(onAction: (EditorAction) -> Unit) {
    val uriHandler = LocalUriHandler.current
    Box {
        var showHelpMenu by remember { mutableStateOf(false) }
        TopBarMenuButton(
            text = stringResource(Res.string.top_bar_help),
            onClick = { showHelpMenu = true }
        )
        DropdownMenu(
            expanded = showHelpMenu,
            onDismissRequest = { showHelpMenu = false },
            offset = DpOffset(0.dp, 4.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MenuItemWithIcon(
                text = stringResource(Res.string.top_bar_help_keyboard_shortcuts),
                icon = Icons.Default.Keyboard,
                onClick = {
                    onAction(EditorAction.ShowShortcuts)
                    showHelpMenu = false
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
            MenuItemWithIcon(
                text = stringResource(Res.string.top_bar_help_documentation),
                icon = Icons.AutoMirrored.Default.Help,
                onClick = {
                    uriHandler.openUri("https://github.com/Ma7moud3ly/nemo-editor")
                    showHelpMenu = false
                }
            )
            MenuItemWithIcon(
                text = stringResource(Res.string.top_bar_help_about),
                icon = Icons.Default.Info,
                onClick = {
                    onAction(EditorAction.ShowAbout)
                    showHelpMenu = false
                }
            )
        }
    }
}

@Composable
private fun MenuItemWithIcon(
    text: String,
    icon: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (enabled) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = text,
                    fontSize = 13.sp,
                    color = if (enabled) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.tertiary
                )
            }
        },
        onClick = onClick,
        enabled = enabled,
        colors = MenuDefaults.itemColors(
            textColor = MaterialTheme.colorScheme.onPrimary,
            disabledTextColor = MaterialTheme.colorScheme.tertiary
        ),
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}
