package io.ma7moud3ly.nemo.tabs

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.model.toColorScheme

private data class PreviewFile(
    override val name: String,
    override val path: String = name
) : TabFile

@Preview
@Composable
private fun NemoTabsPreview() {
    val tabs = listOf("main.kt", "App.kt", "notes.md").mapIndexed { index, name ->
        NemoTab(
            id = index.toString(),
            tabFile = PreviewFile(name),
            codeState = CodeState(language = Language.KOTLIN, isDirty = index == 0)
        )
    }
    MaterialTheme(colorScheme = EditorThemes.NEMO_LIGHT.toColorScheme()) {
        Surface {
            NemoTabs(
                tabs = tabs,
                activeTabId = "0",
                onSelect = {},
                onClose = {}
            )
        }
    }
}

/**
 * A scrolling row of tabs. Each tab shows an icon, a title, a dot when it has
 * unsaved changes, and a close button. Nothing is drawn when [tabs] is empty.
 *
 * @param tabs the open tabs, in order
 * @param activeTabId id of the tab drawn as active
 * @param onSelect called when a tab is clicked
 * @param onClose called when a tab's close button is clicked
 * @param modifier applied to the row's surface
 * @param closeLabel accessibility label of the close button
 * @param icon the icon shown before the title
 */
@Composable
fun NemoTabs(
    tabs: List<NemoTab>,
    activeTabId: String?,
    onSelect: (NemoTab) -> Unit,
    onClose: (NemoTab) -> Unit,
    modifier: Modifier = Modifier,
    closeLabel: String = "Close",
    icon: @Composable (NemoTab) -> Unit = { EditorTabIcon() }
) {
    if (tabs.isEmpty()) return

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = modifier.fillMaxWidth()
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
                    title = tab.title,
                    isActive = tab.id == activeTabId,
                    isDirty = tab.isDirty,
                    closeLabel = closeLabel,
                    icon = { icon(tab) },
                    onClick = { onSelect(tab) },
                    onClose = { onClose(tab) }
                )
            }
        }
    }
}

/**
 * The default tab icon, a file glyph.
 *
 * @param tint color of the icon
 */
@Composable
fun EditorTabIcon(tint: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Icon(
        imageVector = Icons.AutoMirrored.Default.InsertDriveFile,
        contentDescription = null,
        modifier = Modifier.size(16.dp),
        tint = tint
    )
}

@Composable
private fun TabItem(
    title: String,
    isActive: Boolean,
    isDirty: Boolean,
    closeLabel: String,
    icon: @Composable () -> Unit,
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
            icon()

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            if (isDirty) {
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
                    contentDescription = closeLabel,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
