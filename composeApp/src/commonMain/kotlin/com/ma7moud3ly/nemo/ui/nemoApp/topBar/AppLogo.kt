package com.ma7moud3ly.nemo.ui.nemoApp.topBar

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.nemo.platform.emojiFontFontFamily
import com.ma7moud3ly.nemo.themes.EditorThemes
import com.ma7moud3ly.nemo.themes.AppTheme
import nemoeditor.composeapp.generated.resources.Res
import nemoeditor.composeapp.generated.resources.logo_flipped
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun NemoAnimatedLogoPreview() {
    AppTheme(theme = EditorThemes.NEMO_LIGHT) {
        NemoAnimatedLogo()
    }
}

@Preview
@Composable
private fun NemoStaticLogoPreview() {
    AppTheme(theme = EditorThemes.NEMO_LIGHT) {
        NemoStaticLogo()
    }
}


@Composable
internal fun NemoAnimatedLogo(
    modifier: Modifier = Modifier.height(36.dp).widthIn(max = 80.dp),
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nemo_animation")

    // Fish swimming animation (longer horizontal movement - goes beyond box edges)
    val fishOffsetX by infiniteTransition.animateFloat(
        initialValue = -45f,  // Start further left (outside box)
        targetValue = 45f,    // End further right (outside box)
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutCubic),  // Slower, smoother motion
            repeatMode = RepeatMode.Reverse
        ),
        label = "fish_offset_x"
    )

    // Fish vertical bobbing (subtle up and down)
    val fishOffsetY by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fish_offset_y"
    )

    // Fish rotation (tilts as it swims)
    val fishRotation by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fish_rotation"
    )

    // Bubble floating animation
    val bubbleOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubble_offset_y"
    )

    // Bubble opacity (fades out as it rises)
    val bubbleAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubble_alpha"
    )

    // Coral swaying
    val coralRotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coral_rotation"
    )

    // Fish scale (breathing effect)
    val fishScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fish_scale"
    )

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            // Background Bubble
            Text(
                text = "🫧",
                fontSize = 14.sp,
                fontFamily = emojiFontFontFamily(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                modifier = Modifier
                    .offset(x = (-10).dp, y = 0.dp)
                    .graphicsLayer {
                        translationY = bubbleOffsetY * 3
                        alpha = bubbleAlpha * 0.6f
                    }
            )

            // Coral (swaying gently)
            Text(
                text = "🪸",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = emojiFontFontFamily(),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 6.dp, y = 2.dp)
                    .rotate(coralRotation)
            )

            // Swimming Fish (Nemo!) - swims beyond box edges
            Image(
                painter = painterResource(Res.drawable.logo_flipped),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .graphicsLayer {
                        translationX = fishOffsetX
                        translationY = fishOffsetY
                        rotationZ = fishRotation
                        scaleX = if (fishOffsetX > 0) fishScale else -fishScale
                        scaleY = fishScale
                    }
            )
            // Foreground Bubble (rising)
            Text(
                text = "🫧",
                fontSize = 12.sp,
                fontFamily = emojiFontFontFamily(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                modifier = Modifier
                    .offset(x = 12.dp, y = 8.dp)
                    .graphicsLayer {
                        translationY = bubbleOffsetY * 5
                        alpha = bubbleAlpha
                        scaleX = 1f - (bubbleAlpha * 0.3f)
                        scaleY = 1f - (bubbleAlpha * 0.3f)
                    }
            )
        }
    }
}

@Composable
internal fun NemoStaticLogo(
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.size(36.dp),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "N",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
