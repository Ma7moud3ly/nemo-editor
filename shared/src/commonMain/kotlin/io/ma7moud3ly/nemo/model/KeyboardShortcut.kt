package io.ma7moud3ly.nemo.model

import androidx.compose.ui.graphics.vector.ImageVector

data class KeyboardShortcut(
    val keys: String,
    val description: String,
    val category: String,
    val icon: ImageVector? = null
)