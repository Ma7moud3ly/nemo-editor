package io.ma7moud3ly.nemo.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.noto_color_emoji_regular
import org.jetbrains.compose.resources.Font

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

@Composable
actual fun emojiFontFontFamily(): FontFamily? {
    return FontFamily(Font(Res.font.noto_color_emoji_regular))
}
