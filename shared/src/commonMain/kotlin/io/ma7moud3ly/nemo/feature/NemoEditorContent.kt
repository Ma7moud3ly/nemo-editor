package io.ma7moud3ly.nemo.feature

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
import androidx.compose.ui.tooling.preview.Preview
import io.ma7moud3ly.nemo.NemoCodeEditor
import io.ma7moud3ly.nemo.feature.bottomBar.EditorBottomBar
import io.ma7moud3ly.nemo.feature.sideBar.EditorSidebar
import io.ma7moud3ly.nemo.feature.topBar.EditorTopBar
import io.ma7moud3ly.nemo.feature.welcome.WelcomeScreen
import io.ma7moud3ly.nemo.managers.FilesManager
import io.ma7moud3ly.nemo.managers.LanguagesManager
import io.ma7moud3ly.nemo.managers.ShortcutsManager
import io.ma7moud3ly.nemo.managers.file
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorAction
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.model.NemoFile
import io.ma7moud3ly.nemo.model.UiState
import io.ma7moud3ly.nemo.search.FindReplaceBar
import io.ma7moud3ly.nemo.shared.resources.Res
import io.ma7moud3ly.nemo.shared.resources.editor_tabs_close
import io.ma7moud3ly.nemo.tabs.NemoTab
import io.ma7moud3ly.nemo.tabs.EditorTabIcon
import io.ma7moud3ly.nemo.tabs.NemoTabs
import io.ma7moud3ly.nemo.tabs.TabsManager
import io.ma7moud3ly.nemo.ui.AppTheme
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun NemoEditorScreenContentPreview() {
    val theme = EditorThemes.NEMO_DARK
    val editorSettings = EditorSettings(theme = theme)
    val tabsManager = TabsManager(
        initialTabs = listOf(
            NemoTab(
                id = "1",
                codeState = CodeState(
                    initialCode = "fun main() {\n    println(\"Hello, Nemo!\")\n}",
                    language = Language.KOTLIN
                ),
                tabFile = NemoFile("main.py", "")
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
                NemoTabs(
                    tabs = tabsManager.tabs,
                    activeTabId = activeTab?.id,
                    onSelect = { onAction(EditorAction.SwitchTab(it.id)) },
                    onClose = { onAction(EditorAction.CloseTab(it.id)) },
                    closeLabel = stringResource(Res.string.editor_tabs_close),
                    icon = { EditorTabIcon(tint = it.file.iconColor()) }
                )
                if (showCodeEditor) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AnimatedVisibility(
                            visible = showFindAndReplace,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            FindReplaceBar(
                                state = codeState!!,
                                showReplace = true,
                                onDismiss = { onAction(EditorAction.ToggleFind) },
                            )
                        }
                        NemoCodeEditor(
                            state = codeState!!,
                            settings = editorSettings,
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
