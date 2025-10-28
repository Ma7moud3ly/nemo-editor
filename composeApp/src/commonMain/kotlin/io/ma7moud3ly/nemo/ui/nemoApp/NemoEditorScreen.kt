package io.ma7moud3ly.nemo.ui.nemoApp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal fun NemoEditorScreen(viewModel: NemoEditorViewModel) {

    val editorSettings = remember { viewModel.editorSettings }
    val tabsManager = remember { viewModel.tabsManager }
    val filesManager = remember { viewModel.filesManager }
    val shortcutsManager = remember { viewModel.shortcutsManager }
    val languagesManager = remember { viewModel.languagesManager }

    NemoEditorScreenContent(
        shortcutsManager = shortcutsManager,
        languagesManager = languagesManager,
        tabsManager = tabsManager,
        filesManager = filesManager,
        editorSettings = editorSettings,
        uiState = viewModel.uiState,
        onAction = viewModel::handleEditorAction
    )
}