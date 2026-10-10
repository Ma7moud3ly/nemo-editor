package io.ma7moud3ly.nemo.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.toColorScheme
import io.ma7moud3ly.nemo.platform.ConfigureSystemBars
import io.ma7moud3ly.nemo.platform.LocalPlatform
import io.ma7moud3ly.nemo.platform.getPlatform

@Composable
internal fun AppTheme(
    theme: EditorTheme = EditorThemes.NEMO_LIGHT,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalPlatform provides getPlatform()) {
        val materialTheme = theme.toColorScheme()
        ConfigureSystemBars(
            statusBarColor = materialTheme.surface,
            navigationBarColor = materialTheme.surface,
            darkTheme = theme.dark
        )
        MaterialTheme(
            colorScheme = materialTheme,
            content = content
        )
    }
}