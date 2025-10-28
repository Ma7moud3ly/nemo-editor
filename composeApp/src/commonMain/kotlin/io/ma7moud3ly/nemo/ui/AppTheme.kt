package io.ma7moud3ly.nemo.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.platform.ConfigureSystemBars
import io.ma7moud3ly.nemo.platform.LocalPlatform
import io.ma7moud3ly.nemo.platform.getPlatform

private fun Long.toColor() = Color(this)


/**
 * Creates and returns a Material Design [ColorScheme] derived from the EditorTheme.
 * It intelligently selects properties from the theme to map to Material color roles.
 *
 * @return A [ColorScheme] (either light or dark) that is thematically consistent with the editor.
 */
private fun EditorTheme.toMaterialColorScheme(): ColorScheme {
    val primaryColor = this.background.toColor()
    val secondaryColor = this.syntax.keyword.toColor()
    val tertiaryColor = this.lineNumber.toColor()
    val backgroundColor = this.background.toColor()
    val foregroundColor = this.foreground.toColor()
    val surfaceColor = this.gutter.toColor()
    val surfaceTint = this.syntax.function.toColor()

    return if (this.dark) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = foregroundColor,
            secondary = secondaryColor,
            onSecondary = Color.White,
            tertiary = tertiaryColor,
            onTertiary = Color.Black,
            background = backgroundColor,
            onBackground = foregroundColor,
            surface = surfaceColor,
            onSurface = foregroundColor,
            surfaceTint = surfaceTint,
            onError = Color.White
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = foregroundColor,
            secondary = secondaryColor,
            onSecondary = Color.Black,
            tertiary = tertiaryColor,
            onTertiary = Color.White,
            background = backgroundColor,
            onBackground = foregroundColor,
            surface = surfaceColor,
            onSurface = foregroundColor,
            surfaceTint = surfaceTint,
            onError = Color.White
        )
    }
}

@Composable
internal fun AppTheme(
    theme: EditorTheme = EditorThemes.NEMO_LIGHT,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalPlatform provides getPlatform()) {
        val materialTheme = theme.toMaterialColorScheme()
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