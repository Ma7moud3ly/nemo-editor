package io.ma7moud3ly.nemo.ui.nemoApp.sideBar

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.ma7moud3ly.nemo.platform.LocalPlatform
import io.ma7moud3ly.nemo.platform.isJvm
import io.ma7moud3ly.nemo.managers.FilesManager
import io.ma7moud3ly.nemo.model.EditorAction
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.ui.AppTheme
import io.ma7moud3ly.nemo.ui.nemoApp.sideBar.explorer.ExplorerContent
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.sidebar_collapse
import nemoeditor.composeapp.generated.resources.sidebar_settings
import nemoeditor.composeapp.generated.resources.sidebar_themes
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun SidebarPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        Surface(modifier = Modifier.fillMaxSize()) {
            var expanded by remember { mutableStateOf(true) }
            EditorSidebar(
                isExpanded = { expanded },
                onAction = {},
                filesManager = FilesManager()
            )
        }
    }
}

@Composable
internal fun EditorSidebar(
    isExpanded: () -> Boolean,
    onAction: (EditorAction) -> Unit,
    filesManager: FilesManager,
    modifier: Modifier = Modifier
) {
    val platform = LocalPlatform.current
    var activeTab by remember {
        mutableStateOf(
            if (platform.isJvm) SidebarTab.Explorer
            else SidebarTab.Search
        )
    }
    val isExpanded = isExpanded()

    Row(modifier = modifier) {
        // Icon Bar (Always visible)
        IconsBar(
            onAction = onAction,
            activeTab = if (isExpanded) activeTab else null,
            onTabSelected = { tab ->
                if (activeTab == tab && isExpanded) {
                    onAction(EditorAction.ShowSideBar(false))
                } else {
                    activeTab = tab
                    onAction(EditorAction.ShowSideBar(true))
                }
            }
        )

        // Expandable Panel
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            SidebarPanel(
                tab = activeTab,
                onCollapse = { onAction(EditorAction.ToggleSideBar) },
                onAction = onAction,
                filesManager = filesManager
            )
        }
    }
}

@Composable
private fun IconsBar(
    activeTab: SidebarTab?,
    onAction: (EditorAction) -> Unit,
    onTabSelected: (SidebarTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val platform = LocalPlatform.current
    val icons = remember {
        if (platform.isJvm) SidebarTab.entries
        else SidebarTab.entries.drop(1)
    }
    Surface(
        modifier = modifier.fillMaxHeight().width(56.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Logo Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "N",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            // Tab Icons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                icons.forEach { tab ->
                    IconBarButton(
                        icon = tab.icon,
                        label = tab.label,
                        isActive = tab == activeTab,
                        onClick = { onTabSelected(tab) }
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            IconBarButton(
                icon = Icons.Default.Palette,
                label = stringResource(Res.string.sidebar_themes),
                isActive = false,
                onClick = { onAction(EditorAction.ToggleTheme) }
            )
            // Settings at bottom
            IconBarButton(
                icon = Icons.Default.Settings,
                label = stringResource(Res.string.sidebar_settings),
                isActive = false,
                onClick = { onAction(EditorAction.OpenSettings) }
            )
        }
    }
}

@Composable
private fun IconBarButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick)
            .background(
                if (isActive) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                else Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(3.dp)
                    .background(MaterialTheme.colorScheme.secondary)
                    .align(Alignment.CenterStart)
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SidebarPanel(
    tab: SidebarTab,
    onCollapse: () -> Unit,
    onAction: (EditorAction) -> Unit,
    filesManager: FilesManager
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Panel Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    tab.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        stringResource(Res.string.sidebar_collapse),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            // Panel Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (tab) {
                    SidebarTab.Explorer -> ExplorerContent(onAction, filesManager)
                    SidebarTab.Search -> SearchContent()
                    SidebarTab.SourceControl -> SourceControlContent()
                    SidebarTab.Extensions -> ExtensionsContent()
                    SidebarTab.Run -> RunContent()
                }
            }
        }
    }
}


