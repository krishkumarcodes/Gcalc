package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    DARK, LIGHT, AMOLED, AUTO
}

@Immutable
data class GlassColors(
    val isDark: Boolean,
    val accent: Color,
    val accentContainer: Color,
    val background: Color,
    val surfaceGlass: Color,
    val surfaceGlassSecondary: Color,
    val borderGlass: Color,
    val borderGlassHighlight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val keyNeutral: Color,
    val keyOperator: Color,
    val keyAction: Color,
    val keyEquals: Color,
    val blob1: Color,
    val blob2: Color,
    val blob3: Color
)

val LocalGlassColors = staticCompositionLocalOf {
    createGlassColors(ThemeMode.DARK, AccentColor.CYAN.color, true)
}

fun createGlassColors(themeMode: ThemeMode, accentColor: Color, systemDark: Boolean): GlassColors {
    return com.example.store.ThemeEngine.generateGlassPalette(themeMode, accentColor, systemDark)
}

@Composable
fun GcalcTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    accentColor: Color = Color(0xFF06B6D4),
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val glassColors = createGlassColors(themeMode, accentColor, systemDark)

    val colorScheme = if (glassColors.isDark) {
        darkColorScheme(
            primary = glassColors.accent,
            onPrimary = Color.White,
            background = glassColors.background,
            surface = glassColors.surfaceGlass,
            onSurface = glassColors.textPrimary
        )
    } else {
        lightColorScheme(
            primary = glassColors.accent,
            onPrimary = Color.White,
            background = glassColors.background,
            surface = glassColors.surfaceGlass,
            onSurface = glassColors.textPrimary
        )
    }

    CompositionLocalProvider(LocalGlassColors provides glassColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

@Composable
fun GlasscalcTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    accentColor: AccentColor = AccentColor.CYAN,
    content: @Composable () -> Unit
) = GcalcTheme(themeMode, accentColor.color, content)
