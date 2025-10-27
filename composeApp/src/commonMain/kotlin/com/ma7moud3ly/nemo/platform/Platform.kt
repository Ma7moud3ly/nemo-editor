package com.ma7moud3ly.nemo.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.CoroutineDispatcher

enum class PlatformType {
    ANDROID, WASM_JS, JVM, IOS
}

interface Platform {
    val name: String
    val type: PlatformType
}

fun mockPlatform(type: PlatformType): Platform = object : Platform {
    override val name: String = "test"
    override val type: PlatformType get() = type
}

expect fun getPlatform(): Platform

val Platform.isWasmJs get() = this.type == PlatformType.WASM_JS
val Platform.isJvm get() = this.type == PlatformType.JVM
val Platform.isAndroid get() = this.type == PlatformType.ANDROID
val Platform.isIos get() = this.type == PlatformType.IOS
val Platform.isMobile get() = isAndroid || isIos

val LocalPlatform = compositionLocalOf { getPlatform() }

@Composable
expect fun ConfigureSystemBars(
    statusBarColor: Color,
    navigationBarColor: Color,
    darkTheme: Boolean
)

expect fun ioDispatcher(): CoroutineDispatcher

@Composable
expect fun emojiFontFontFamily(): FontFamily?