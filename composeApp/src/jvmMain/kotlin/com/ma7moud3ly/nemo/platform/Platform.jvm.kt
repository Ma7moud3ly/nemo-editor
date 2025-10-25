package com.ma7moud3ly.nemo.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val type: PlatformType = PlatformType.JVM
}

actual fun getPlatform(): Platform = JVMPlatform()

@Composable
actual fun ConfigureSystemBars(
    statusBarColor: Color,
    navigationBarColor: Color,
    darkTheme: Boolean
) {

}

actual fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
