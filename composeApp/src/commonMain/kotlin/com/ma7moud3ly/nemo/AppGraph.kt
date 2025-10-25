package com.ma7moud3ly.nemo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.ma7moud3ly.nemo.model.AppRoutes
import com.ma7moud3ly.nemo.model.EditorAction
import com.ma7moud3ly.nemo.ui.nemoApp.NemoEditorScreen
import com.ma7moud3ly.nemo.ui.nemoApp.NemoEditorViewModel
import com.ma7moud3ly.nemo.ui.nemoApp.dialogs.AboutNemoDialog
import com.ma7moud3ly.nemo.ui.nemoApp.dialogs.KeyboardShortcutsDialog
import com.ma7moud3ly.nemo.ui.nemoApp.dialogs.SettingsDialog
import com.ma7moud3ly.nemo.ui.nemoApp.dialogs.ThemeSelectorDialog
import com.ma7moud3ly.nemo.ui.nemoApp.dialogs.UnsavedChangesDialog
import com.ma7moud3ly.nemo.ui.nemoApp.sideBar.explorer.CreateItemDialog
import com.ma7moud3ly.nemo.ui.nemoApp.sideBar.explorer.DeleteConfirmationDialog
import com.ma7moud3ly.nemo.ui.nemoApp.sideBar.explorer.FileDetailsDialog
import com.ma7moud3ly.nemo.ui.nemoApp.sideBar.explorer.RenameDialog

@Composable
internal fun AppGraph(viewModel: NemoEditorViewModel) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            println("Navigating to: $event")
            println(viewModel.selectedFile.value)
            navController.navigate(event)
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.HomeScreen
    ) {
        composable<AppRoutes.HomeScreen> {
            NemoEditorScreen(viewModel = viewModel)
        }

        dialog<AppRoutes.Dialog.About> {
            AboutNemoDialog(
                appVersion = viewModel.appVersion,
                buildNumber = viewModel.buildNumber,
                onDismiss = { navController.popBackStack() }
            )
        }
        dialog<AppRoutes.Dialog.Themes> {
            var theme by remember { viewModel.editorSettings.themeState }
            ThemeSelectorDialog(
                currentTheme = theme,
                onThemeSelected = {
                    theme = it
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }
        dialog<AppRoutes.Dialog.Settings> {
            SettingsDialog(
                settings = viewModel.editorSettings,
                onDismiss = { navController.popBackStack() }
            )
        }
        dialog<AppRoutes.Dialog.Shortcuts> {
            KeyboardShortcutsDialog(
                onDismiss = { navController.popBackStack() }
            )
        }
        dialog<AppRoutes.Dialog.UnSavedChanged> {
            var pendingAction by remember { viewModel.pendingAction }
            UnsavedChangesDialog(
                onSave = {
                    viewModel.handleEditorAction(EditorAction.Save)
                    pendingAction?.invoke()
                    viewModel.resetPendingAction()
                    navController.popBackStack()
                }, onExport = {
                    viewModel.handleEditorAction(EditorAction.Export)
                    pendingAction?.invoke()
                    viewModel.resetPendingAction()
                    navController.popBackStack()
                },
                onDiscard = {
                    pendingAction?.invoke()
                    viewModel.resetPendingAction()
                    navController.popBackStack()
                },
                onCancel = {
                    viewModel.resetPendingAction()
                    navController.popBackStack()
                }
            )
        }

        dialog<AppRoutes.Dialog.File.Details> {
            val file = viewModel.selectedFile.value
            if (file != null) FileDetailsDialog(
                file = file,
                onDismiss = { navController.popBackStack() }
            )
        }
        dialog<AppRoutes.Dialog.File.Delete> {
            val file = viewModel.selectedFile.value
            if (file != null) DeleteConfirmationDialog(
                file = file,
                onDismiss = { navController.popBackStack() },
                onConfirm = {
                    viewModel.deleteFile(file)
                    navController.popBackStack()
                }
            )

        }
        dialog<AppRoutes.Dialog.File.Rename> {
            val file = viewModel.selectedFile.value
            if (file != null) RenameDialog(
                file = file,
                onDismiss = { navController.popBackStack() },
                onConfirm = { newName ->
                    viewModel.renameFile(file, newName)
                    navController.popBackStack()
                }
            )
        }
        dialog<AppRoutes.Dialog.File.Create> {
            CreateItemDialog(
                title = "New File",
                label = "File name",
                onDismiss = { navController.popBackStack() },
                onConfirm = { name ->
                    viewModel.createNewFile(name)
                    navController.popBackStack()
                }
            )
        }
        dialog<AppRoutes.Dialog.Folder.Create> {
            CreateItemDialog(
                title = "New Folder",
                label = "Folder name",
                onDismiss = { navController.popBackStack() },
                onConfirm = { name ->
                    viewModel.createNewFolder(name)
                    navController.popBackStack()
                }
            )
        }
    }
}

