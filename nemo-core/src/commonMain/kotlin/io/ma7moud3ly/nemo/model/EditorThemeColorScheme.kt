package io.ma7moud3ly.nemo.model

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Builds a Material [ColorScheme] from this editor theme, so the app around
 * the editor uses the same colors as the editor itself.
 *
 * The result is a dark scheme when [EditorTheme.dark] is true, and a light
 * scheme otherwise.
 */
fun EditorTheme.toColorScheme(): ColorScheme {
    val primaryColor = Color(background)
    val secondaryColor = Color(syntax.keyword)
    val tertiaryColor = Color(lineNumber)
    val backgroundColor = Color(background)
    val foregroundColor = Color(foreground)
    val surfaceColor = Color(gutter)
    val surfaceTint = Color(syntax.function)

    return if (dark) {
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
