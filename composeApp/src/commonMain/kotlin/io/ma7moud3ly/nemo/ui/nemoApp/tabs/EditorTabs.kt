package io.ma7moud3ly.nemo.ui.nemoApp.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ma7moud3ly.nemo.managers.TabsManager
import io.ma7moud3ly.nemo.model.EditorAction
import io.ma7moud3ly.nemo.model.EditorTab
import io.ma7moud3ly.nemo.model.NemoFile
import io.ma7moud3ly.nemo.ui.AppTheme
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.ui.nemoApp.iconColor
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.editor_tabs_close
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun EditorTabsPreview() {
    val tabsManager = TabsManager(
        activeTabId = "1",
        initialTabs = listOf(
            EditorTab(
                id = "1",
                file = NemoFile(name = "main.kt"),
                isDirty = true
            ),
            EditorTab(
                id = "2",
                file = NemoFile(name = "main.kt"),
                isDirty = false
            ), EditorTab(
                id = "3",
                file = NemoFile(name = "main.kt"),
                isDirty = false
            )
        )
    )
    AppTheme(theme = EditorThemes.NEMO_LIGHT) {
        Surface {
            EditorTabs(
                activeTab = { tabsManager.activeTab },
                tabs = { tabsManager.tabs },
                onAction = {}
            )
        }
    }
}

@Composable
internal fun EditorTabs(
    activeTab: () -> EditorTab?,
    tabs: () -> List<EditorTab>,
    onAction: (EditorAction) -> Unit
) {
    val tabs = tabs()
    val activeTab = activeTab()
    if (tabs.isEmpty()) return

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            tabs.forEach { tab ->
                TabItem(
                    tab = tab,
                    isActive = tab.id == activeTab?.id,
                    onClick = { onAction(EditorAction.SwitchTab(tab.id)) },
                    onClose = { onAction(EditorAction.CloseTab(tab.id)) }
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    tab: EditorTab,
    isActive: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isActive)
            MaterialTheme.colorScheme.surface
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(
            topStart = 6.dp,
            topEnd = 6.dp
        ),
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.InsertDriveFile,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = tab.file.iconColor()
            )

            Text(
                text = tab.file.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            if (tab.codeState.contentChanged) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            CircleShape
                        )
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.editor_tabs_close),
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
