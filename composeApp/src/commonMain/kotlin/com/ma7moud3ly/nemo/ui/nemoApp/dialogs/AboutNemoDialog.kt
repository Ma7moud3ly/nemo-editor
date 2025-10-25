package com.ma7moud3ly.nemo.ui.nemoApp.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ma7moud3ly.nemo.themes.AppTheme
import com.ma7moud3ly.nemo.themes.EditorThemes
import com.ma7moud3ly.nemo.themes.emojiFontFontFamily
import com.ma7moud3ly.nemo.ui.nemoApp.welcome.AppSlogan
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.about_dialog_build
import nemoeditor.composeapp.generated.resources.about_dialog_close
import nemoeditor.composeapp.generated.resources.about_dialog_copyright
import nemoeditor.composeapp.generated.resources.about_dialog_description
import nemoeditor.composeapp.generated.resources.about_dialog_footer
import nemoeditor.composeapp.generated.resources.about_dialog_github
import nemoeditor.composeapp.generated.resources.about_dialog_license
import nemoeditor.composeapp.generated.resources.about_dialog_version
import nemoeditor.composeapp.generated.resources.about_dialog_website
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun AboutDialogPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        AboutNemoDialog(
            appVersion = "1.0.0",
            buildNumber = "42",
            onDismiss = {}
        )
    }
}


@Composable
internal fun AboutNemoDialog(
    appVersion: String,
    buildNumber: String = "",
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 450.dp)
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box {
                    // Background decoration
                    DialogBackgroundDecoration()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Close button
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(Res.string.about_dialog_close),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        AppSlogan()
                        Spacer(Modifier.height(32.dp))

                        SectionVersion(
                            appVersion = appVersion,
                            buildNumber = buildNumber
                        )

                        Spacer(Modifier.height(24.dp))

                        // Description
                        Text(
                            stringResource(Res.string.about_dialog_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(Modifier.height(24.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )

                        Spacer(Modifier.height(16.dp))
                        Footer()
                        Spacer(Modifier.height(8.dp))
                        // Copyright
                        Text(
                            stringResource(Res.string.about_dialog_copyright),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionVersion(
    appVersion: String,
    buildNumber: String
) {
    // Version info card
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow(label = stringResource(Res.string.about_dialog_version), value = appVersion)
            if (buildNumber.isNotEmpty()) {
                InfoRow(label = stringResource(Res.string.about_dialog_build), value = buildNumber)
            }
        }
    }
}

@Composable
private fun Footer() {
    val uriHandler = LocalUriHandler.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            stringResource(Res.string.about_dialog_footer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontFamily = emojiFontFontFamily()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = {
                uriHandler.openUri("https://github.com/Ma7moud3ly/nemo-editor/blob/main/LICENSE")
            }) {
                Text(
                    stringResource(Res.string.about_dialog_license),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                "•",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            TextButton(
                onClick = {
                    uriHandler.openUri("https://github.com/Ma7moud3ly/nemo-editor")
                }
            ) {
                Text(
                    stringResource(Res.string.about_dialog_github),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                "•",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            TextButton(
                onClick = {
                    uriHandler.openUri("https://nemo-editor.web.app")
                }
            ) {
                Text(
                    stringResource(Res.string.about_dialog_website),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun DialogBackgroundDecoration() {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Top right accent
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 100.dp, y = (-80).dp)
                .rotate(45f)
                .align(Alignment.TopEnd)
                .alpha(0.6f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Bottom left accent
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-70).dp, y = 70.dp)
                .align(Alignment.BottomStart)
                .alpha(0.6f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}
