package io.ma7moud3ly.nemo.ui.nemoApp.sideBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.sidebar_explorer
import nemoeditor.composeapp.generated.resources.sidebar_extensions
import nemoeditor.composeapp.generated.resources.sidebar_run
import nemoeditor.composeapp.generated.resources.sidebar_search
import nemoeditor.composeapp.generated.resources.sidebar_source_control
import org.jetbrains.compose.resources.stringResource


internal enum class SidebarTab(val icon: ImageVector, val label: String) {
    Explorer(Icons.Default.FolderOpen, "Explorer"),
    Search(Icons.Default.Search, "Search"),
    SourceControl(Icons.Default.AccountCircle, "Source Control"),
    Extensions(Icons.Default.Apps, "Extensions"),
    Run(Icons.Default.PlayArrow, "Run & Debug");

    val title: String
        @Composable
        get() = when (this) {
            Explorer -> stringResource(Res.string.sidebar_explorer)
            Search -> stringResource(Res.string.sidebar_search)
            SourceControl -> stringResource(Res.string.sidebar_source_control)
            Extensions -> stringResource(Res.string.sidebar_extensions)
            Run -> stringResource(Res.string.sidebar_run)
        }
}