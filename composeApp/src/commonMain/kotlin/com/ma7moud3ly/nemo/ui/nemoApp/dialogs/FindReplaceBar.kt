package com.ma7moud3ly.nemo.ui.nemoApp.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.nemo.managers.FindAndReplaceManager
import com.ma7moud3ly.nemo.managers.rememberFindAndReplaceManager
import com.ma7moud3ly.nemo.model.CodeState
import com.ma7moud3ly.nemo.model.Language
import com.ma7moud3ly.nemo.themes.AppTheme
import com.ma7moud3ly.nemo.themes.EditorThemes
import com.ma7moud3ly.nemo.ui.nemoApp.isCompactDevice
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.find_replace_bar_case_sensitive
import nemoeditor.composeapp.generated.resources.find_replace_bar_close
import nemoeditor.composeapp.generated.resources.find_replace_bar_find
import nemoeditor.composeapp.generated.resources.find_replace_bar_find_compact
import nemoeditor.composeapp.generated.resources.find_replace_bar_matches
import nemoeditor.composeapp.generated.resources.find_replace_bar_next
import nemoeditor.composeapp.generated.resources.find_replace_bar_next_match
import nemoeditor.composeapp.generated.resources.find_replace_bar_no_results
import nemoeditor.composeapp.generated.resources.find_replace_bar_previous
import nemoeditor.composeapp.generated.resources.find_replace_bar_previous_match
import nemoeditor.composeapp.generated.resources.find_replace_bar_regex
import nemoeditor.composeapp.generated.resources.find_replace_bar_replace
import nemoeditor.composeapp.generated.resources.find_replace_bar_replace_all
import nemoeditor.composeapp.generated.resources.find_replace_bar_replace_all_compact
import nemoeditor.composeapp.generated.resources.find_replace_bar_replace_compact
import nemoeditor.composeapp.generated.resources.find_replace_bar_replace_current
import nemoeditor.composeapp.generated.resources.find_replace_bar_toggle_replace
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Preview(widthDp = 800)
@Composable
private fun FindReplaceBarPreview() {
    val state = CodeState(
        language = Language.KOTLIN,
        initialCode = """
        fun main() {
            println("Hello, World!")
        }
        """".trimIndent()
    )
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            FindReplaceBar(
                codeState = state,
                showReplace = true,
                onDismiss = {}
            )
        }
    }
}

/**
 *  Find and Replace Bar
 */
@Composable
fun FindReplaceBar(
    codeState: CodeState,
    showReplace: Boolean = false,
    onDismiss: () -> Unit
) {
    val manager = rememberFindAndReplaceManager(codeState)
    var isReplaceVisible by remember { mutableStateOf(showReplace) }

    LaunchedEffect(
        manager.findText,
        manager.caseSensitive,
        manager.wholeWord,
        manager.useRegex,
        codeState.code
    ) {
        manager.updateSearch()
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(if (isCompactDevice()) 8.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompactDevice()) 8.dp else 12.dp)
        ) {
            if (isCompactDevice()) {
                CompactSearchRow(
                    manager = manager,
                    onDismiss = onDismiss,
                    isReplaceVisible = isReplaceVisible,
                    onToggleReplace = { isReplaceVisible = !isReplaceVisible }
                )

                AnimatedVisibility(
                    visible = isReplaceVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    CompactReplaceRow(manager = manager)
                }
            } else {
                SearchRow(
                    manager = manager,
                    onDismiss = onDismiss,
                    isReplaceVisible = isReplaceVisible,
                    onToggleReplace = { isReplaceVisible = !isReplaceVisible }
                )

                AnimatedVisibility(
                    visible = isReplaceVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    ReplaceRow(manager = manager)
                }
            }
        }
    }
}

@Composable
private fun SearchRow(
    manager: FindAndReplaceManager,
    onDismiss: () -> Unit,
    isReplaceVisible: Boolean,
    onToggleReplace: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Input
        OutlinedTextField(
            value = manager.findText,
            onValueChange = { manager.findText = it },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            placeholder = {
                Text(
                    stringResource(Res.string.find_replace_bar_find),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (manager.findText.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        // Match counter
                        Text(
                            if (manager.hasMatches())
                                stringResource(
                                    Res.string.find_replace_bar_matches,
                                    manager.currentMatchIndex + 1,
                                    manager.matches.size
                                )
                            else stringResource(Res.string.find_replace_bar_no_results),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = if (manager.hasMatches())
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                cursorColor = MaterialTheme.colorScheme.secondary
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Navigation - Prev
        IconButton(
            onClick = { manager.goToPrevious() },
            enabled = manager.hasMatches(),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                stringResource(Res.string.find_replace_bar_previous_match),
                tint = if (manager.hasMatches())
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }

        // Navigation - Next
        IconButton(
            onClick = { manager.goToNext() },
            enabled = manager.hasMatches(),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                stringResource(Res.string.find_replace_bar_next_match),
                tint = if (manager.hasMatches())
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }

        // Case sensitive
        IconButton(
            onClick = { manager.caseSensitive = !manager.caseSensitive },
            modifier = Modifier.size(48.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (manager.caseSensitive)
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                else Color.Transparent
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(Res.string.find_replace_bar_case_sensitive),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (manager.caseSensitive)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Regex
        IconButton(
            onClick = { manager.useRegex = !manager.useRegex },
            modifier = Modifier.size(48.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (manager.useRegex)
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                else Color.Transparent
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(Res.string.find_replace_bar_regex),
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (manager.useRegex)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Replace Toggle
        IconButton(
            onClick = onToggleReplace,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                if (isReplaceVisible) Icons.Default.FindReplace else Icons.Default.Search,
                contentDescription = stringResource(Res.string.find_replace_bar_toggle_replace),
                tint = if (isReplaceVisible)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Close Button
        IconButton(
            onClick = {
                manager.clear()
                onDismiss()
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Close,
                stringResource(Res.string.find_replace_bar_close),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReplaceRow(manager: FindAndReplaceManager) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Replace Input
        OutlinedTextField(
            value = manager.replaceText,
            onValueChange = { manager.replaceText = it },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            placeholder = {
                Text(
                    stringResource(Res.string.find_replace_bar_replace),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.FindReplace,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                cursorColor = MaterialTheme.colorScheme.secondary
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Replace Current Button
        IconButton(
            onClick = { manager.replaceCurrent() },
            enabled = manager.hasMatches(),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.FindReplace,
                stringResource(Res.string.find_replace_bar_replace_current),
                tint = if (manager.hasMatches())
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }

        // Replace All Button
        IconButton(
            onClick = { manager.replaceAll() },
            enabled = manager.hasMatches(),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.DoneAll,
                stringResource(Res.string.find_replace_bar_replace_all),
                tint = if (manager.hasMatches())
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }

        // Spacer to align with close button
        Spacer(Modifier.width(48.dp))
    }
}


@Composable
private fun CompactSearchRow(
    manager: FindAndReplaceManager,
    onDismiss: () -> Unit,
    isReplaceVisible: Boolean,
    onToggleReplace: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search Input with Close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = manager.findText,
                onValueChange = { manager.findText = it },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                placeholder = {
                    Text(
                        stringResource(Res.string.find_replace_bar_find_compact),
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    cursorColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp)
            )

            IconButton(
                onClick = {
                    manager.clear()
                    onDismiss()
                },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    stringResource(Res.string.find_replace_bar_close),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Match counter
            if (manager.findText.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (manager.hasMatches())
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    else
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                ) {
                    Text(
                        if (manager.hasMatches())
                            stringResource(
                                Res.string.find_replace_bar_matches,
                                manager.currentMatchIndex + 1,
                                manager.matches.size
                            )
                        else "0",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (manager.hasMatches())
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(2.dp)
            ) {
                IconButton(
                    onClick = { manager.goToPrevious() },
                    enabled = manager.hasMatches(),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        stringResource(Res.string.find_replace_bar_previous),
                        modifier = Modifier.size(18.dp),
                        tint = if (manager.hasMatches())
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }

                IconButton(
                    onClick = { manager.goToNext() },
                    enabled = manager.hasMatches(),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        stringResource(Res.string.find_replace_bar_next),
                        modifier = Modifier.size(18.dp),
                        tint = if (manager.hasMatches())
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
            }

            // Options
            Row(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(2.dp)
            ) {
                IconButton(
                    onClick = { manager.caseSensitive = !manager.caseSensitive },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(
                        stringResource(Res.string.find_replace_bar_case_sensitive),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (manager.caseSensitive)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { manager.useRegex = !manager.useRegex },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(
                        stringResource(Res.string.find_replace_bar_regex),
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (manager.useRegex)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Replace toggle
            FilledTonalIconButton(
                onClick = onToggleReplace,
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (isReplaceVisible)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isReplaceVisible)
                        MaterialTheme.colorScheme.onSecondary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    if (isReplaceVisible) Icons.Default.FindReplace else Icons.Default.Search,
                    contentDescription = stringResource(Res.string.find_replace_bar_toggle_replace),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CompactReplaceRow(manager: FindAndReplaceManager) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Replace Input
        OutlinedTextField(
            value = manager.replaceText,
            onValueChange = { manager.replaceText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = {
                Text(
                    stringResource(Res.string.find_replace_bar_replace_compact),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.FindReplace,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                cursorColor = MaterialTheme.colorScheme.secondary
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedButton(
                onClick = { manager.replaceCurrent() },
                enabled = manager.hasMatches(),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
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
                    stringResource(Res.string.find_replace_bar_replace),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = { manager.replaceAll() },
                enabled = manager.hasMatches(),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
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
                    stringResource(Res.string.find_replace_bar_replace_all_compact),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
