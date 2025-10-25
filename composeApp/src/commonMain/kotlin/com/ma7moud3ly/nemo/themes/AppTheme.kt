package com.ma7moud3ly.nemo.themes

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.ma7moud3ly.nemo.platform.ConfigureSystemBars
import com.ma7moud3ly.nemo.platform.LocalPlatform
import com.ma7moud3ly.nemo.platform.getPlatform
import com.ma7moud3ly.nemo.platform.isWasmJs
import com.ma7moud3ly.nemo.model.EditorTheme
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.noto_color_emoji_regular
import org.jetbrains.compose.resources.Font

private fun Long.toColor() = Color(this)

@Composable
internal fun emojiFontFontFamily(): FontFamily? {
    val platform = LocalPlatform.current
    return if (platform.isWasmJs) FontFamily(Font(Res.font.noto_color_emoji_regular))
    else null
}

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