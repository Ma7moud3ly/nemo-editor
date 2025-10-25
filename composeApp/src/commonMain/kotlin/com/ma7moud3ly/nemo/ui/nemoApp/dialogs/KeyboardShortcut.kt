package com.ma7moud3ly.nemo.ui.nemoApp.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.FormatIndentDecrease
import androidx.compose.material.icons.filled.FormatIndentIncrease
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.nemo.themes.EditorThemes
import com.ma7moud3ly.nemo.model.KeyboardShortcut
import com.ma7moud3ly.nemo.themes.AppTheme
import com.ma7moud3ly.nemo.ui.nemoApp.MyDialog
import org.jetbrains.compose.ui.tooling.preview.Preview


@Preview
@Composable
private fun KeyboardShortcutsDialogPreview() {
    AppTheme(theme = EditorThemes.NEMO_LIGHT) {
        KeyboardShortcutsDialog(
            onDismiss = {}
        )
    }
}

private val groupedShortcuts = listOf(
    // Undo/Redo
    KeyboardShortcut("Ctrl + Z", "Undo", "Edit", Icons.AutoMirrored.Default.Undo),
    KeyboardShortcut("Ctrl + Y", "Redo", "Edit", Icons.AutoMirrored.Default.Redo),

    // Find & Replace
    KeyboardShortcut("Ctrl + F", "Find", "Search", Icons.Default.Search),
    KeyboardShortcut("Ctrl + H", "Find & Replace", "Search", Icons.Default.FindReplace),

    // Format
    KeyboardShortcut("Ctrl + Shift + F", "Format Code", "Format", Icons.Default.Code),
    KeyboardShortcut("Ctrl + Plus", "Zoom In", "Format", Icons.Default.ZoomIn),
    KeyboardShortcut("Ctrl + Minus", "Zoom Out", "Format", Icons.Default.ZoomOut),

    // File Operations
    KeyboardShortcut("Ctrl + S", "Save", "File", Icons.Default.Save),
    KeyboardShortcut("Ctrl + O", "Open", "File", Icons.Default.FolderOpen),
    KeyboardShortcut("Ctrl + N", "New File", "File", Icons.Default.Add),

    // Line Operations
    KeyboardShortcut("Ctrl + D", "Duplicate Line", "Edit", Icons.Default.ContentCopy),
    KeyboardShortcut("Ctrl + L", "Delete Line", "Edit", Icons.Default.Delete),
    KeyboardShortcut("Ctrl + /", "Toggle Comment", "Edit", Icons.Default.Comment),

    // Indentation
    KeyboardShortcut("Ctrl + ]", "Indent", "Format", Icons.Default.FormatIndentIncrease),
    KeyboardShortcut("Ctrl + [", "Unindent", "Format", Icons.Default.FormatIndentDecrease),

    // Selection
    KeyboardShortcut("Ctrl + A", "Select All", "Edit", Icons.Default.SelectAll)
).groupBy { it.category }

@Composable
internal fun KeyboardShortcutsDialog(
    onDismiss: () -> Unit
) {
    MyDialog(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.9f),
        onDismiss = onDismiss,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Keyboard Shortcuts",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Boost your productivity",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Shortcuts List
        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedShortcuts.forEach { (category, categoryShortcuts) ->
                item {
                    ShortcutCategory(
                        category = category,
                        shortcuts = categoryShortcuts
                    )
                }
            }
        }

        // Footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Got it!")
            }
        }
    }
}

@Composable
private fun ShortcutCategory(
    category: String,
    shortcuts: List<KeyboardShortcut>
) {
    Column {
        // Category header
        Text(
            text = category,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Shortcuts in category
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            shortcuts.forEach { shortcut ->
                ShortcutItem(shortcut = shortcut)
            }
        }
    }
}

@Composable
private fun ShortcutItem(
    shortcut: KeyboardShortcut
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon + Description
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (shortcut.icon != null) {
                Icon(
                    imageVector = shortcut.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = shortcut.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Keyboard keys
        KeyBadge(keys = shortcut.keys)
    }
}

@Composable
private fun KeyBadge(keys: String) {
    val keyParts = keys.split(" + ", " / ")

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        keyParts.forEachIndexed { index, key ->
            if (key == "/") {
                Text(
                    text = "or",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                    ),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = key.trim(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (index < keyParts.size - 1 && keyParts[index + 1] != "/") {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}