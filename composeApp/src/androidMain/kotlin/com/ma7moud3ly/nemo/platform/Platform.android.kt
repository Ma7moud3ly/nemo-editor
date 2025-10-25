package com.ma7moud3ly.nemo.platform

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val type: PlatformType = PlatformType.ANDROID
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Suppress("DEPRECATION")
@Composable
actual fun ConfigureSystemBars(
    statusBarColor: Color,
    navigationBarColor: Color,
    darkTheme: Boolean
) {
    val view = LocalView.current
    val window = LocalActivity.current?.window
    if (view.isInEditMode.not() && window != null) {
        SideEffect {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                window.statusBarColor = statusBarColor.toArgb()
                window.navigationBarColor = navigationBarColor.toArgb()
            }
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = darkTheme.not()
            windowInsetsController.isAppearanceLightNavigationBars = darkTheme.not()
        }
    }
}

actual fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

@Composable
actual fun emojiFontFontFamily(): FontFamily? = null
