package io.ma7moud3ly.nemo.ui.nemoApp.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.managers.LanguagesManager
import io.ma7moud3ly.nemo.model.EditorAction
import io.ma7moud3ly.nemo.platform.LocalPlatform
import io.ma7moud3ly.nemo.platform.PlatformType
import io.ma7moud3ly.nemo.platform.isJvm
import io.ma7moud3ly.nemo.platform.mockPlatform
import io.ma7moud3ly.nemo.ui.AppTheme
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.ui.nemoApp.isCompactDevice
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.logo
import nemoeditor.composeapp.generated.resources.welcome_screen_feature_lightweight_description
import nemoeditor.composeapp.generated.resources.welcome_screen_feature_lightweight_title
import nemoeditor.composeapp.generated.resources.welcome_screen_feature_multiplatform_description
import nemoeditor.composeapp.generated.resources.welcome_screen_feature_multiplatform_title
import nemoeditor.composeapp.generated.resources.welcome_screen_feature_smart_description
import nemoeditor.composeapp.generated.resources.welcome_screen_feature_smart_title
import nemoeditor.composeapp.generated.resources.welcome_screen_new_file
import nemoeditor.composeapp.generated.resources.welcome_screen_open_file
import nemoeditor.composeapp.generated.resources.welcome_screen_open_folder
import nemoeditor.composeapp.generated.resources.welcome_screen_slogan
import nemoeditor.composeapp.generated.resources.welcome_screen_why_nemo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Preview()
@Composable
private fun WelcomeScreenPreview() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        WelcomeScreen(
            languagesManager = LanguagesManager(),
            onAction = {}
        )
    }
}

@Preview(widthDp = 700, heightDp = 500)
@Composable
private fun WelcomeScreenPreviewDesktop() {
    AppTheme(theme = EditorThemes.NEMO_DARK) {
        CompositionLocalProvider(
            LocalPlatform provides mockPlatform(PlatformType.JVM)
        ) {
            WelcomeScreen(
                languagesManager = LanguagesManager(),
                onAction = {}
            )
        }
    }
}


@Composable
internal fun WelcomeScreen(
    languagesManager: LanguagesManager,
    onAction: (EditorAction) -> Unit
) {
    val platform = LocalPlatform.current
    val isPreview = LocalInspectionMode.current
    var showNewFileSection by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(isPreview) }

    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    if (showNewFileSection) {
        NewFileSection(
            supportedLanguages = languagesManager.supportedLanguages,
            onCreateFile = { fileName ->
                onAction(EditorAction.NewFile(fileName))
                showNewFileSection = false
            },
            onCreateBlank = {
                onAction(EditorAction.NewUntitledFile)
                showNewFileSection = false
            },
            onDismiss = { showNewFileSection = false }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Geometric background elements
        BackgroundDecoration()

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                Spacer(Modifier.weight(0.3f))

                AppSlogan()

                Spacer(Modifier.height(48.dp))

                ActionGrid(
                    onNewFile = { showNewFileSection = true },
                    onAction = onAction,
                    showFolderPicker = platform.isJvm,
                    modifier = Modifier.widthIn(max = 500.dp)
                )

                Spacer(Modifier.height(56.dp))

                // Feature Highlights
                if (!isCompactDevice()) {
                    FeatureHighlights()
                }

                Spacer(Modifier.weight(0.5f))
            }
        }
    }
}

@Composable
private fun BackgroundDecoration() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top right accent
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = 200.dp, y = (-150).dp)
                .rotate(45f)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Bottom left accent
        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(x = (-150).dp, y = 150.dp)
                .align(Alignment.BottomStart)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
internal fun AppSlogan() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Logo with subtle decoration
        Box(
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        CircleShape
                    )
            )
            Image(
                painter = painterResource(Res.drawable.logo),
                modifier = Modifier.size(80.dp),
                contentDescription = null,
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Nemo",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 2.sp
            )

            Text(
                stringResource(Res.string.welcome_screen_slogan),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun ActionGrid(
    modifier: Modifier = Modifier,
    onNewFile: () -> Unit,
    onAction: (EditorAction) -> Unit,
    showFolderPicker: Boolean
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Primary action
        Button(
            onClick = onNewFile,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                stringResource(Res.string.welcome_screen_new_file),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Secondary actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryAction(
                icon = Icons.AutoMirrored.Outlined.InsertDriveFile,
                label = stringResource(Res.string.welcome_screen_open_file),
                onClick = { onAction(EditorAction.PickFile) },
                modifier = Modifier.weight(1f)
            )

            if (showFolderPicker) {
                SecondaryAction(
                    icon = Icons.Default.FolderOpen,
                    label = stringResource(Res.string.welcome_screen_open_folder),
                    onClick = { onAction(EditorAction.PickFolder) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SecondaryAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun FeatureHighlights() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.widthIn(max = 600.dp)
    ) {
        Text(
            stringResource(Res.string.welcome_screen_why_nemo),
            style = MaterialTheme.typography.labelMedium,
            letterSpacing = 3.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            fontWeight = FontWeight.SemiBold
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureItem(
                icon = Icons.Outlined.Speed,
                title = stringResource(Res.string.welcome_screen_feature_lightweight_title),
                description = stringResource(Res.string.welcome_screen_feature_lightweight_description)
            )

            FeatureItem(
                icon = Icons.Outlined.AutoAwesome,
                title = stringResource(Res.string.welcome_screen_feature_smart_title),
                description = stringResource(Res.string.welcome_screen_feature_smart_description)
            )

            FeatureItem(
                icon = Icons.Default.Code,
                title = stringResource(Res.string.welcome_screen_feature_multiplatform_title),
                description = stringResource(Res.string.welcome_screen_feature_multiplatform_description)
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
