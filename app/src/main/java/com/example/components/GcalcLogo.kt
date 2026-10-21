package com.example.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SpaceGroteskFontFamily
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Version 1: GcalcMark
 * The C symbol only, transparent background, frosted glass style.
 * Features:
 * - Thick rounded ring with opening on right side
 * - Round line caps
 * - Subtle calculator dot inside opening
 * - Frosted glass translucent fill and glowing border
 * - Top-curve specular highlight reflection
 * - Uses dynamic accent color
 */
@Composable
fun GcalcMark(
    modifier: Modifier = Modifier,
    accentColor: Color = LocalGlassColors.current.accent,
    size: Dp = 32.dp,
    animated: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gcalcMarkGlow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w * 0.46f
        val cy = h * 0.50f
        val radius = w * 0.32f
        val strokeW = w * 0.16f

        // 1. Soft accent drop-shadow / glow behind the C
        drawArc(
            color = accentColor.copy(alpha = if (animated) pulseAlpha * 0.35f else 0.30f),
            startAngle = 40f,
            sweepAngle = 280f,
            useCenter = false,
            topLeft = Offset(cx - radius, cy - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeW * 1.5f, cap = StrokeCap.Round)
        )

        // 2. Frosted glass translucent body of the C
        drawArc(
            color = Color.White.copy(alpha = 0.25f),
            startAngle = 40f,
            sweepAngle = 280f,
            useCenter = false,
            topLeft = Offset(cx - radius, cy - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )

        // 3. Bright gradient rim highlight (top-left to bottom-right)
        val rimBrush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.90f),
                Color.White.copy(alpha = 0.30f),
                accentColor.copy(alpha = 0.50f)
            ),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        )
        drawArc(
            brush = rimBrush,
            startAngle = 40f,
            sweepAngle = 280f,
            useCenter = false,
            topLeft = Offset(cx - radius, cy - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeW * 0.28f, cap = StrokeCap.Round)
        )

        // 4. Specular gloss curved highlight reflection across top curve
        val glossBrush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.10f)
            ),
            start = Offset(cx - radius, cy - radius),
            end = Offset(cx + radius * 0.5f, cy)
        )
        drawArc(
            brush = glossBrush,
            startAngle = 200f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(cx - radius, cy - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeW * 0.35f, cap = StrokeCap.Round)
        )

        // 5. Calculator Dot in the opening (subtly says "calculator")
        val dotRadius = w * 0.075f
        val dotCenter = Offset(w * 0.77f, cy)

        // Dot glow
        drawCircle(
            color = accentColor.copy(alpha = 0.45f),
            radius = dotRadius * 1.5f,
            center = dotCenter
        )
        // Dot body
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, accentColor),
                center = dotCenter,
                radius = dotRadius
            ),
            radius = dotRadius,
            center = dotCenter
        )
        // Dot specular highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = dotRadius * 0.35f,
            center = Offset(dotCenter.x - dotRadius * 0.3f, dotCenter.y - dotRadius * 0.3f)
        )
    }
}

/**
 * Version 2: GcalcIcon
 * The C inside the Apple-style rounded-square gradient (app icon).
 */
@Composable
fun GcalcIcon(
    modifier: Modifier = Modifier,
    accentColor: Color = LocalGlassColors.current.accent,
    size: Dp = 64.dp
) {
    val cornerRadius = size * 0.22f

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E1B4B), // Deep indigo
                        Color(0xFF312E81),
                        accentColor.copy(alpha = 0.90f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.10f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        GcalcMark(
            size = size * 0.64f,
            accentColor = accentColor
        )
    }
}

/**
 * Version 3: GcalcLogo
 * Horizontal logo: Mark on left, "Gcalc" text on right in Space Grotesk.
 */
@Composable
fun GcalcLogo(
    modifier: Modifier = Modifier,
    accentColor: Color = LocalGlassColors.current.accent,
    isDark: Boolean = LocalGlassColors.current.isDark,
    markSize: Dp = 32.dp,
    fontSize: Float = 22f
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GcalcMark(
            size = markSize,
            accentColor = accentColor
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "G",
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize.sp,
                color = accentColor
            )
            Text(
                text = "calc",
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize.sp,
                color = if (isDark) Color.White else Color(0xFF111827)
            )
        }
    }
}

/**
 * Version 4: GcalcFlatMark
 * Flat single-color version of the mark for small monochrome usage.
 */
@Composable
fun GcalcFlatMark(
    modifier: Modifier = Modifier,
    tint: Color = LocalGlassColors.current.textPrimary,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w * 0.46f
        val cy = h * 0.50f
        val radius = w * 0.32f
        val strokeW = w * 0.16f

        drawArc(
            color = tint,
            startAngle = 40f,
            sweepAngle = 280f,
            useCenter = false,
            topLeft = Offset(cx - radius, cy - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )

        drawCircle(
            color = tint,
            radius = w * 0.08f,
            center = Offset(w * 0.77f, cy)
        )
    }
}
