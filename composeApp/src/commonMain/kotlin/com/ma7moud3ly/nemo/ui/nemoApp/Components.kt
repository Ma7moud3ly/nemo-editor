package com.ma7moud3ly.nemo.ui.nemoApp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
internal fun MyDialog(
    background: Color = MaterialTheme.colorScheme.background,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = isCompactDevice().not()
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(if (isCompactDevice()) 0.9f else 1f),
            shape = MaterialTheme.shapes.medium,
            color = background,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .then(modifier),
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement,
                content = content
            )
        }
    }
}

@Composable
internal fun isCompactDevice(): Boolean {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
}

@Composable
internal fun isMediumDevice(): Boolean {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
}
