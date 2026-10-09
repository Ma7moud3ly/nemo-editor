package io.ma7moud3ly.nemo.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.ma7moud3ly.nemo.managers.FindAndReplaceManager
import io.ma7moud3ly.nemo.managers.rememberFindAndReplaceManager
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.model.toColorScheme
import io.ma7moud3ly.nemo.search.resources.Res
import io.ma7moud3ly.nemo.search.resources.find_replace_close
import io.ma7moud3ly.nemo.search.resources.find_replace_find
import io.ma7moud3ly.nemo.search.resources.find_replace_match_case
import io.ma7moud3ly.nemo.search.resources.find_replace_matches
import io.ma7moud3ly.nemo.search.resources.find_replace_next_match
import io.ma7moud3ly.nemo.search.resources.find_replace_no_results
import io.ma7moud3ly.nemo.search.resources.find_replace_previous_match
import io.ma7moud3ly.nemo.search.resources.find_replace_regex
import io.ma7moud3ly.nemo.search.resources.find_replace_replace
import io.ma7moud3ly.nemo.search.resources.find_replace_replace_all
import io.ma7moud3ly.nemo.search.resources.find_replace_title
import io.ma7moud3ly.nemo.search.resources.find_replace_whole_word
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun FindReplaceDialogPreview() {
    val state = CodeState(
        language = Language.KOTLIN,
        initialCode = "fun main() {\n    println(\"Hello, World!\")\n}"
    )
    val manager = remember(state) {
        FindAndReplaceManager(state).apply { findText = "main" }
    }
    MaterialTheme(colorScheme = EditorThemes.NEMO_DARK.toColorScheme()) {
        FindReplaceDialogContent(
            manager = manager,
            showReplace = true,
            onClose = {}
        )
    }
}

/**
 * Find and replace dialog, shown on top of the editor.
 *
 * It offers the same search as [FindReplaceBar] in a floating window, with
 * the options (match case, whole word, regex) written out as chips. Enter in
 * the find field moves to the next match, and Enter in the replace field
 * replaces the current one.
 *
 * @param state the editor state to search in
 * @param onDismiss called when the user closes the dialog
 * @param modifier applied to the dialog's surface
 * @param showReplace whether the replace field and buttons are shown
 * @param manager holds the search text, options and matches
 */
@Composable
fun FindReplaceDialog(
    state: CodeState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showReplace: Boolean = true,
    manager: FindAndReplaceManager = rememberFindAndReplaceManager(state)
) {
    SearchOnChange(state, manager)

    val close = {
        manager.clear()
        onDismiss()
    }

    Dialog(onDismissRequest = close) {
        FindReplaceDialogContent(
            manager = manager,
            showReplace = showReplace,
            onClose = close,
            modifier = modifier
        )
    }
}

@Composable
private fun FindReplaceDialogContent(
    manager: FindAndReplaceManager,
    showReplace: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val findFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { findFocus.requestFocus() }

    Surface(
        modifier = modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Header(title = stringResource(Res.string.find_replace_title), closeLabel = stringResource(Res.string.find_replace_close), onClose = onClose)

            DialogField(
                value = manager.findText,
                onValueChange = { manager.findText = it },
                label = stringResource(Res.string.find_replace_find),
                icon = Icons.Default.Search,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { manager.goToNext() }),
                modifier = Modifier.focusRequester(findFocus)
            )

            if (showReplace) {
                DialogField(
                    value = manager.replaceText,
                    onValueChange = { manager.replaceText = it },
                    label = stringResource(Res.string.find_replace_replace),
                    icon = Icons.Default.FindReplace,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { manager.replaceCurrent() })
                )
            }

            MatchNavigation(manager = manager)

            SearchOptions(manager = manager)

            if (showReplace) {
                ReplaceActions(manager = manager)
            }
        }
    }
}

@Composable
private fun Header(title: String, closeLabel: String, onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.FindReplace,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onClose, modifier = Modifier.size(40.dp)) {
            Icon(
                Icons.Default.Close,
                contentDescription = closeLabel,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DialogField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = FontFamily.Monospace
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            focusedLabelColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            cursorColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

/** The match counter with the previous and next buttons. */
@Composable
private fun MatchNavigation(manager: FindAndReplaceManager) {
    val hasMatches = manager.hasMatches()
    val navigationTint = if (hasMatches) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when {
                manager.findText.isEmpty() -> ""
                hasMatches -> stringResource(
                    Res.string.find_replace_matches,
                    manager.currentMatchIndex + 1,
                    manager.matches.size
                )

                else -> stringResource(Res.string.find_replace_no_results)
            },
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = if (hasMatches || manager.findText.isEmpty())
                MaterialTheme.colorScheme.onSurfaceVariant
            else
                MaterialTheme.colorScheme.error
        )
        IconButton(
            onClick = { manager.goToPrevious() },
            enabled = hasMatches,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(Res.string.find_replace_previous_match),
                tint = navigationTint
            )
        }
        IconButton(
            onClick = { manager.goToNext() },
            enabled = hasMatches,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(Res.string.find_replace_next_match),
                tint = navigationTint
            )
        }
    }
}

/** Chips for match case, whole word and regex. */
@Composable
private fun SearchOptions(manager: FindAndReplaceManager) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OptionChip(
            label = stringResource(Res.string.find_replace_match_case),
            selected = manager.caseSensitive,
            onClick = { manager.caseSensitive = !manager.caseSensitive }
        )
        OptionChip(
            label = stringResource(Res.string.find_replace_whole_word),
            selected = manager.wholeWord,
            onClick = { manager.wholeWord = !manager.wholeWord }
        )
        OptionChip(
            label = stringResource(Res.string.find_replace_regex),
            selected = manager.useRegex,
            onClick = { manager.useRegex = !manager.useRegex }
        )
    }
}

@Composable
private fun OptionChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        colors = FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
            selectedLabelColor = MaterialTheme.colorScheme.secondary
        )
    )
}

/** The replace and replace-all buttons. */
@Composable
private fun ReplaceActions(manager: FindAndReplaceManager) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = { manager.replaceCurrent() },
            enabled = manager.hasMatches(),
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                Icons.Default.FindReplace,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                stringResource(Res.string.find_replace_replace),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        Button(
            onClick = { manager.replaceAll() },
            enabled = manager.hasMatches(),
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                Icons.Default.DoneAll,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                stringResource(Res.string.find_replace_replace_all),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
