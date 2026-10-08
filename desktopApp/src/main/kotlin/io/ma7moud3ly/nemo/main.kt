package io.ma7moud3ly.nemo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.ma7moud3ly.nemo.shared.resources.Res
import io.ma7moud3ly.nemo.shared.resources.app_name
import io.ma7moud3ly.nemo.shared.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.logo),
        state = rememberWindowState(
            placement = WindowPlacement.Maximized
        )
    ) {
        NemoEditorApp()
    }
}
