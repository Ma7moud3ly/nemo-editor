package com.ma7moud3ly.nemo.ui.nemoApp.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.nemo.model.EditorTheme
import com.ma7moud3ly.nemo.themes.AppTheme
import com.ma7moud3ly.nemo.themes.EditorThemes
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.theme_selector_close
import nemoeditor.composeapp.generated.resources.theme_selector_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ThemeSelectorDialogPreview() {
    val theme = EditorThemes.NEMO_LIGHT
    AppTheme(theme) {
        ThemeSelectorDialog(
            currentTheme = theme,
            onThemeSelected = {},
            onDismiss = {}
        )
    }
}

@Composable
internal fun ThemeSelectorDialog(
    currentTheme: EditorTheme,
    onThemeSelected: (EditorTheme) -> Unit,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()
    val themes = remember { EditorThemes.ALL_THEMES }
    LaunchedEffect(currentTheme) {
        val index = themes.indexOfFirst { it.name == currentTheme.name }
        if (index != -1) listState.scrollToItem(index)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.theme_selector_title)) },
        text = {
            LazyColumn(
                state = listState,
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(themes) { theme ->
                    ThemePreviewCard(
                        theme = theme,
                        isSelected = theme.name == currentTheme.name,
                        onClick = { onThemeSelected(theme) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(Res.string.theme_selector_close))
            }
        }
    )
}

@Composable
private fun ThemePreviewCard(
    theme: EditorTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(theme.background)
        ),
        border = if (isSelected) BorderStroke(
            width = 2.dp,
            MaterialTheme.colorScheme.secondary
        ) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                theme.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color(theme.syntax.keyword)
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ColorBox(Color(theme.syntax.keyword))
                ColorBox(Color(theme.syntax.string))
                ColorBox(Color(theme.syntax.comment))
                ColorBox(Color(theme.syntax.number))
                ColorBox(Color(theme.syntax.function))
                ColorBox(Color(theme.syntax.type))
            }
        }
    }
}


@Composable
private fun ColorBox(color: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(color, MaterialTheme.shapes.small)
            .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
    )
}
