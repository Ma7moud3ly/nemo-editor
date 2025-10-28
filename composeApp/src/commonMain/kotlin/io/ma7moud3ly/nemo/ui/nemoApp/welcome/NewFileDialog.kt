package io.ma7moud3ly.nemo.ui.nemoApp.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.model.LanguageDetails
import io.ma7moud3ly.nemo.model.NemoFile
import io.ma7moud3ly.nemo.platform.LocalPlatform
import io.ma7moud3ly.nemo.platform.isWasmJs
import io.ma7moud3ly.nemo.ui.AppTheme
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.ui.nemoApp.MyDialog
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.new_file_dialog_blank_file
import nemoeditor.composeapp.generated.resources.new_file_dialog_clear
import nemoeditor.composeapp.generated.resources.new_file_dialog_close
import nemoeditor.composeapp.generated.resources.new_file_dialog_create_file
import nemoeditor.composeapp.generated.resources.new_file_dialog_create_new_file
import nemoeditor.composeapp.generated.resources.new_file_dialog_enter_filename
import nemoeditor.composeapp.generated.resources.new_file_dialog_example
import nemoeditor.composeapp.generated.resources.new_file_dialog_file_name
import nemoeditor.composeapp.generated.resources.new_file_dialog_include_extension
import nemoeditor.composeapp.generated.resources.new_file_dialog_language
import nemoeditor.composeapp.generated.resources.new_file_dialog_search_languages
import nemoeditor.composeapp.generated.resources.new_file_dialog_select_language
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun NewFileSectionPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                NewFileSection(
                    supportedLanguages = listOf(),
                    onCreateFile = {},
                    onCreateBlank = {},
                    onDismiss = {}
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewFileSection(
    supportedLanguages: List<LanguageDetails>,
    onCreateFile: (file: NemoFile) -> Unit,
    onCreateBlank: () -> Unit,
    onDismiss: () -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf<LanguageDetails?>(null) }

    fun createFile() {
        val language = selectedLanguage ?: return
        val file = NemoFile(
            name = fileName,
            extension = language.extension,
            path = ""
        )
        onCreateFile(file)
    }

    // Auto-detect language from file extension
    LaunchedEffect(fileName) {
        if (fileName.contains('.')) {
            val ext = fileName.substringAfterLast('.').lowercase()
            val detectedLanguage = supportedLanguages.find { it.extension == ext }
            if (detectedLanguage != null && detectedLanguage != selectedLanguage) {
                selectedLanguage = detectedLanguage
            }
        }
    }

    val canCreate = fileName.isNotEmpty() &&
            fileName.contains('.') &&
            selectedLanguage != null

    MyDialog(
        onDismiss = onDismiss,
        background = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(Res.string.new_file_dialog_create_new_file),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(Res.string.new_file_dialog_enter_filename),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(Res.string.new_file_dialog_close),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            FileNameInput(
                fileName = { fileName },
                onSetFileName = { fileName = it },
                onCreateFile = { if (canCreate) createFile() }
            )

            LanguageSelector(
                supportedLanguages = supportedLanguages,
                selectedLanguage = { selectedLanguage },
                onSelectLanguage = { selectedLanguage = it },
                fileName = { fileName },
                onSetFileName = { fileName = it }
            )

            ActionButtons(
                canCreate = canCreate,
                onCreateFile = ::createFile,
                onCreateBlank = onCreateBlank
            )
            // Helper text
            AnimatedVisibility(
                visible = fileName.isNotEmpty() && !fileName.contains('.'),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = stringResource(Res.string.new_file_dialog_include_extension),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun FileNameInput(
    fileName: () -> String,
    onSetFileName: (String) -> Unit,
    onCreateFile: () -> Unit
) {
    val fileName = fileName()
    val focusRequester = remember { FocusRequester() }

    // Focus on text field when section appears
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // File name input
    OutlinedTextField(
        value = fileName,
        onValueChange = onSetFileName,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        label = {
            Text(
                text = stringResource(Res.string.new_file_dialog_file_name), color = MaterialTheme.colorScheme.onPrimary
            )
        },
        placeholder = { Text(stringResource(Res.string.new_file_dialog_example)) },
        leadingIcon = {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        trailingIcon = {
            if (fileName.isNotEmpty()) {
                IconButton(onClick = { onSetFileName("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = stringResource(Res.string.new_file_dialog_clear),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onCreateFile() })
    )
}

@Composable
private fun LanguageSelector(
    supportedLanguages: List<LanguageDetails>,
    selectedLanguage: () -> LanguageDetails?,
    onSelectLanguage: (LanguageDetails) -> Unit,
    fileName: () -> String,
    onSetFileName: (String) -> Unit
) {
    val fileName = fileName()
    val selectedLanguage = selectedLanguage()
    var showLanguageSelector by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Language list
    val filteredLanguages by remember {
        derivedStateOf {
            supportedLanguages.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.extension.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(Res.string.new_file_dialog_language),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLanguageSelector = !showLanguageSelector },
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedLanguage != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LanguageIcon(selectedLanguage)
                        Column {
                            Text(
                                text = selectedLanguage.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = ".${selectedLanguage.extension}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(Res.string.new_file_dialog_select_language),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = if (showLanguageSelector)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Language list dropdown
    AnimatedVisibility(
        visible = showLanguageSelector,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                // Search box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text(stringResource(Res.string.new_file_dialog_search_languages)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary
                    )
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )



                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredLanguages) { lang ->
                        LanguageItem(
                            language = lang,
                            isSelected = selectedLanguage == selectedLanguage,
                            onClick = {
                                onSelectLanguage(lang)
                                showLanguageSelector = false
                                searchQuery = ""
                                // Auto-append extension if not present
                                if (!fileName.endsWith(".${lang.extension}")) {
                                    val nameWithoutExt =
                                        fileName.substringBeforeLast('.').ifEmpty { fileName }
                                    onSetFileName("$nameWithoutExt.${lang.extension}")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    canCreate: Boolean,
    onCreateFile: () -> Unit,
    onCreateBlank: () -> Unit
) {
    val platform = LocalPlatform.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (platform.isWasmJs.not()) OutlinedButton(
            onClick = onCreateBlank,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            BasicText(
                text = stringResource(Res.string.new_file_dialog_blank_file),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                autoSize = TextAutoSize.StepBased(8.sp, 14.sp)
            )
        }

        Button(
            onClick = onCreateFile,
            enabled = canCreate,
            modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            BasicText(
                text = stringResource(Res.string.new_file_dialog_create_file), style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                autoSize = TextAutoSize.StepBased(8.sp, 14.sp)
            )
        }
    }

}

@Composable
private fun LanguageIcon(language: LanguageDetails) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(language.color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = language.icon,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun LanguageItem(
    language: LanguageDetails,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected)
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        else
            Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LanguageIcon(language)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = language.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ".${language.extension}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = language.description,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
