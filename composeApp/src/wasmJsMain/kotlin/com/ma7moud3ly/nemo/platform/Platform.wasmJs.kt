package com.ma7moud3ly.nemo.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val type: PlatformType = PlatformType.WASM_JS
}


actual fun getPlatform(): Platform = WasmPlatform()


@Composable
actual fun ConfigureSystemBars(
    statusBarColor: Color,
    navigationBarColor: Color,
    darkTheme: Boolean
) {

}

actual fun ioDispatcher(): CoroutineDispatcher = Dispatchers.Default
