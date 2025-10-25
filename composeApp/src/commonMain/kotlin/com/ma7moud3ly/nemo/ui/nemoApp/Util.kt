package com.ma7moud3ly.nemo.ui.nemoApp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ma7moud3ly.nemo.model.NemoFile

internal val directoryColor = Color(0xFFEFE578)

@Composable
internal fun NemoFile.iconColor(): Color {
    return if (this.isDirectory) directoryColor
    else when (this.extension.lowercase()) {
        "kt", "kts" -> MaterialTheme.colorScheme.secondary
        "java" -> Color(0xFFE76F00)
        "py" -> Color(0xFF3776AB)
        "js", "ts" -> Color(0xFFF7DF1E)
        "html", "htm" -> Color(0xFFE34C26)
        "css", "scss" -> Color(0xFF264DE4)
        "json" -> Color(0xFFCBCB41)
        "xml" -> Color(0xFF0060AC)
        "md" -> Color(0xFFCCCCCC)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}
