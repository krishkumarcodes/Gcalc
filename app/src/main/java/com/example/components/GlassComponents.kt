package com.example.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily

/**
 * Animated soft mesh gradient background with slow drifting blobs.
 */
@Composable
fun MeshGradientBackground(
    animate: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = LocalGlassColors.current

    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    val animOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (animate) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1"
    )
    val animOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (animate) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(19000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2"
    )

    Box(modifier = modifier.fillMaxSize().background(colors.background)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Blob 1: Accent color
            val c1 = Offset(
                w * (0.25f + 0.35f * animOffset1),
                h * (0.15f + 0.25f * (1f - animOffset2))
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colors.blob1, Color.Transparent),
                    center = c1,
                    radius = w * 0.75f
                ),
                center = c1,
                radius = w * 0.75f
            )

            // Blob 2: Secondary blob
            val c2 = Offset(
                w * (0.75f - 0.30f * animOffset2),
                h * (0.60f + 0.25f * animOffset1)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colors.blob2, Color.Transparent),
                    center = c2,
                    radius = w * 0.85f
                ),
                center = c2,
                radius = w * 0.85f
            )

            // Blob 3: Counter blob
            val c3 = Offset(
                w * (0.5f + 0.2f * (animOffset1 - animOffset2)),
                h * (0.85f - 0.3f * animOffset1)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colors.blob3, Color.Transparent),
                    center = c3,
                    radius = w * 0.65f
                ),
                center = c3,
                radius = w * 0.65f
            )
        }
    }
}

/**
 * Reusable frosted glass panel with top highlight border.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    backgroundColor: Color? = null,
    borderAlpha: Float = 1f,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = LocalGlassColors.current
    val shape = RoundedCornerShape(cornerRadius)
    val bg = backgroundColor ?: colors.surfaceGlass

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            colors.borderGlassHighlight.copy(alpha = colors.borderGlassHighlight.alpha * borderAlpha * 0.7f),
            colors.borderGlass.copy(alpha = colors.borderGlass.alpha * borderAlpha * 0.4f)
        ),
        start = Offset(0f, 0f),
        end = Offset(200f, 400f)
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = if (colors.isDark) 8.dp else 4.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = colors.accent.copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(bg)
            .border(1.dp, borderBrush, shape)
    ) {
        content()
    }
}

enum class KeyType {
    DIGIT,
    OPERATOR,
    ACTION,
    EQUALS,
    SCIENTIFIC
}

/**
 * Glass Calculator Key with organic circular/pill curves around corners,
 * spring press scale, and clean touch feedback.
 */
@Composable
fun GlassKey(
    text: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    keyType: KeyType = KeyType.DIGIT,
    isActiveOperator: Boolean = false,
    subText: String? = null,
    fontSize: TextUnit = 24.sp,
    height: Dp = 64.dp,
    onLongClick: (() -> Unit)? = null
) {
    val colors = LocalGlassColors.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "keyScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else (if (keyType == KeyType.EQUALS) 8.dp else 2.dp),
        animationSpec = spring(),
        label = "keyElevation"
    )

    val keyBgBase = when (keyType) {
        KeyType.DIGIT -> colors.keyNeutral
        KeyType.OPERATOR -> if (isActiveOperator) colors.accent else colors.keyOperator
        KeyType.ACTION -> colors.keyAction
        KeyType.EQUALS -> colors.keyEquals
        KeyType.SCIENTIFIC -> colors.surfaceGlassSecondary
    }

    val keyBg = if (isPressed) {
        keyBgBase.copy(alpha = (keyBgBase.alpha * 1.25f).coerceAtMost(0.95f))
    } else keyBgBase

    val textColor = when (keyType) {
        KeyType.EQUALS -> Color.White
        KeyType.OPERATOR -> if (isActiveOperator) Color.White else colors.accent
        KeyType.ACTION -> Color(0xFFEF4444)
        KeyType.SCIENTIFIC -> colors.textSecondary
        KeyType.DIGIT -> colors.textPrimary
    }

    // Circular rounded corner shape for buttons
    val shape = RoundedCornerShape(28.dp)

    val borderBrush = Brush.linearGradient(
        colors = if (keyType == KeyType.EQUALS || isActiveOperator) {
            listOf(Color.White.copy(alpha = 0.6f), colors.accent.copy(alpha = 0.8f))
        } else if (isPressed) {
            listOf(colors.accent.copy(alpha = 0.5f), colors.accent.copy(alpha = 0.3f))
        } else {
            listOf(colors.borderGlassHighlight.copy(alpha = 0.4f), colors.borderGlass.copy(alpha = 0.2f))
        }
    )

    val hapticService = runCatching { com.example.service.LocalHapticFeedbackService.current }.getOrNull()

    Box(
        modifier = modifier
            .height(height)
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = shape,
                spotColor = if (keyType == KeyType.EQUALS || isPressed) colors.accent else Color.Transparent
            )
            .clip(shape)
            .background(keyBg)
            .border(1.dp, borderBrush, shape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        hapticService?.performKeyTap(8L)
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() },
                    onLongPress = { onLongClick?.invoke() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = if (keyType == KeyType.EQUALS || keyType == KeyType.OPERATOR) FontWeight.Bold else FontWeight.Medium,
                fontSize = fontSize,
                color = textColor,
                textAlign = TextAlign.Center
            )
            if (subText != null) {
                Text(
                    text = subText,
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 10.sp,
                    color = textColor.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Universal on-screen Glass Number Pad used in Formula inputs and Tools
 * to strictly prevent the Android software keyboard from popping up!
 */
@Composable
fun GlassNumPad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onToggleSign: () -> Unit,
    onDone: () -> Unit,
    onNext: (() -> Unit)? = null,
    onPrev: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
        cornerRadius = 28.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Navigation row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (onPrev != null) {
                    GlassKey(
                        text = "◀ Prev",
                        onClick = onPrev,
                        modifier = Modifier.weight(1f),
                        height = 38.dp,
                        fontSize = 13.sp,
                        keyType = KeyType.SCIENTIFIC
                    )
                }
                if (onNext != null) {
                    GlassKey(
                        text = "Next ▶",
                        onClick = onNext,
                        modifier = Modifier.weight(1f),
                        height = 38.dp,
                        fontSize = 13.sp,
                        keyType = KeyType.SCIENTIFIC
                    )
                }
                GlassKey(
                    text = "Clear",
                    onClick = onClear,
                    modifier = Modifier.weight(1f),
                    height = 38.dp,
                    fontSize = 13.sp,
                    keyType = KeyType.ACTION
                )
                GlassKey(
                    text = "Done ✓",
                    onClick = onDone,
                    modifier = Modifier.weight(1f),
                    height = 38.dp,
                    fontSize = 13.sp,
                    keyType = KeyType.EQUALS
                )
            }

            // Row 1: 7, 8, 9, ⌫
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GlassKey("7", { onDigit("7") }, Modifier.weight(1f), height = 44.dp)
                GlassKey("8", { onDigit("8") }, Modifier.weight(1f), height = 44.dp)
                GlassKey("9", { onDigit("9") }, Modifier.weight(1f), height = 44.dp)
                GlassKey("⌫", onBackspace, Modifier.weight(1f), height = 44.dp, keyType = KeyType.ACTION)
            }

            // Row 2: 4, 5, 6, ±
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GlassKey("4", { onDigit("4") }, Modifier.weight(1f), height = 44.dp)
                GlassKey("5", { onDigit("5") }, Modifier.weight(1f), height = 44.dp)
                GlassKey("6", { onDigit("6") }, Modifier.weight(1f), height = 44.dp)
                GlassKey("±", onToggleSign, Modifier.weight(1f), height = 44.dp, keyType = KeyType.SCIENTIFIC)
            }

            // Row 3: 1, 2, 3, .
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GlassKey("1", { onDigit("1") }, Modifier.weight(1f), height = 44.dp)
                GlassKey("2", { onDigit("2") }, Modifier.weight(1f), height = 44.dp)
                GlassKey("3", { onDigit("3") }, Modifier.weight(1f), height = 44.dp)
                GlassKey(".", { onDigit(".") }, Modifier.weight(1f), height = 44.dp)
            }

            // Row 4: 0, 00, e
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GlassKey("0", { onDigit("0") }, Modifier.weight(2f), height = 44.dp)
                GlassKey("00", { onDigit("00") }, Modifier.weight(1f), height = 44.dp)
                GlassKey("e", { onDigit("e") }, Modifier.weight(1f), height = 44.dp, keyType = KeyType.SCIENTIFIC)
            }
        }
    }
}
