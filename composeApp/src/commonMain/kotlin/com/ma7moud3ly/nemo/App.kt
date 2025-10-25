package com.ma7moud3ly.nemo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ma7moud3ly.nemo.themes.AppTheme
import com.ma7moud3ly.nemo.ui.nemoApp.NemoEditorViewModel


@Composable
internal fun CodeEditorApp(
    viewModel: NemoEditorViewModel = viewModel { NemoEditorViewModel() }
) {
    val theme by remember { viewModel.editorSettings.themeState }

    LifecycleResumeEffect(LocalLifecycleOwner) {
        onPauseOrDispose {
            viewModel.saveState()
        }
    }

    AppTheme(theme = theme) {
        Surface(
            modifier = Modifier.fillMaxSize().navigationBarsPadding().imePadding(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppGraph(viewModel)
        }
    }
}

