package com.ma7moud3ly.nemo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.app_name
import nemoeditor.composeapp.generated.resources.logo
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
