package com.ma7moud3ly.nemo.ui.nemoApp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onPreviewKeyEvent
import com.ma7moud3ly.nemo.managers.FilesManager
import com.ma7moud3ly.nemo.managers.LanguagesManager
import com.ma7moud3ly.nemo.managers.ShortcutsManager
import com.ma7moud3ly.nemo.managers.TabsManager
import com.ma7moud3ly.nemo.model.CodeState
import com.ma7moud3ly.nemo.model.EditorAction
import com.ma7moud3ly.nemo.model.EditorSettings
import com.ma7moud3ly.nemo.model.EditorTab
import com.ma7moud3ly.nemo.themes.EditorThemes
import com.ma7moud3ly.nemo.model.Language
import com.ma7moud3ly.nemo.model.NemoFile
import com.ma7moud3ly.nemo.model.UiState
import com.ma7moud3ly.nemo.themes.AppTheme
import com.ma7moud3ly.nemo.ui.editor.NemoCodeEditor
import com.ma7moud3ly.nemo.ui.nemoApp.bottomBar.EditorBottomBar
import com.ma7moud3ly.nemo.ui.nemoApp.dialogs.FindReplaceBar
import com.ma7moud3ly.nemo.ui.nemoApp.sideBar.EditorSidebar
import com.ma7moud3ly.nemo.ui.nemoApp.tabs.EditorTabs
import com.ma7moud3ly.nemo.ui.nemoApp.topBar.EditorTopBar
import com.ma7moud3ly.nemo.ui.nemoApp.welcome.WelcomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun NemoEditorScreenContentPreview() {
    val theme = EditorThemes.NEMO_DARK
    val editorSettings = EditorSettings(theme = theme)
    val tabsManager = TabsManager(
        initialTabs = listOf(
            EditorTab(
                id = "1",
                codeState = CodeState(
                    initialCode = "fun main() {\n    println(\"Hello, Nemo!\")\n}",
                    language = Language.KOTLIN
                ),
                file = NemoFile("main.py", "")
            )
        )
    )
    AppTheme(theme) {
        NemoEditorScreenContent(
            uiState = UiState(),
            filesManager = FilesManager(),
            shortcutsManager = ShortcutsManager(action = {}),
            tabsManager = tabsManager,
            editorSettings = editorSettings,
            languagesManager = LanguagesManager(),
            onAction = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NemoEditorScreenContent(
    uiState: UiState,
    filesManager: FilesManager,
    shortcutsManager: ShortcutsManager,
    tabsManager: TabsManager,
    editorSettings: EditorSettings,
    languagesManager: LanguagesManager,
    onAction: (EditorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTab by tabsManager.activeTabFlow.collectAsState(null)
    val codeState = activeTab?.codeState
    val showFindAndReplace by remember { uiState.showFindAndReplace }
    val showAnimatedLogo by remember { uiState.showAnimatedLogo }
    val sidebarExpanded by remember { uiState.sidebarExpanded }
    val showCodeEditor = activeTab?.codeState != null && tabsManager.tabs.isNotEmpty()
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(showCodeEditor) {
        focusRequester.requestFocus()
    }
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { keyEvent ->
                shortcutsManager.handleKeyEvent(keyEvent)
            },
        topBar = {
            EditorTopBar(
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                tab = activeTab,
                animatedLogo = showAnimatedLogo,
                onAction = onAction
            )
        },
        bottomBar = {
            codeState?.let {
                EditorBottomBar(
                    state = it,
                    settings = editorSettings,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isCompactDevice().not()) {
                EditorSidebar(
                    filesManager = filesManager,
                    isExpanded = { sidebarExpanded },
                    onAction = onAction
                )
            }
            // Main Editor Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Tab Bar
                EditorTabs(
                    activeTab = { activeTab },
                    tabs = { tabsManager.tabs },
                    onAction = onAction
                )
                if (showCodeEditor) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AnimatedVisibility(
                            visible = showFindAndReplace,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            FindReplaceBar(
                                codeState = codeState!!,
                                showReplace = true,
                                onDismiss = { onAction(EditorAction.ToggleFind) },
                            )
                        }
                        NemoCodeEditor(
                            codeState = codeState!!,
                            editorSettings = editorSettings,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else if (!isCompactDevice() || !sidebarExpanded) {
                    WelcomeScreen(
                        languagesManager,
                        onAction = onAction
                    )
                }
            }
        }
    }
}
