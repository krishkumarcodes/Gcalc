package com.example.store

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AccentColor
import com.example.ui.theme.GlassColors
import com.example.ui.theme.ThemeMode
import kotlin.math.max

object ThemeEngine {

    fun generateGlassPalette(
        themeMode: ThemeMode,
        accent: Color,
        systemDark: Boolean
    ): GlassColors {
        val isDark = when (themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.AMOLED -> true
            ThemeMode.AUTO -> systemDark
        }
        val isAmoled = themeMode == ThemeMode.AMOLED

        val bg = if (isDark) {
            if (isAmoled) Color(0xFF000000) else Color(0xFF0F172A)
        } else {
            Color(0xFFF8FAFC)
        }

        val surfaceGlass = if (isDark) {
            if (isAmoled) Color(0x22111827) else Color(0x381E293B)
        } else {
            Color(0xCCFFFFFF)
        }

        val surfaceGlassSecondary = if (isDark) {
            if (isAmoled) Color(0x331F2937) else Color(0x45334155)
        } else {
            Color(0x99F1F5F9)
        }

        val borderGlass = if (isDark) Color(0x4DFFFFFF) else Color(0x33000000)
        val borderGlassHighlight = if (isDark) Color(0xA6FFFFFF) else Color(0x66FFFFFF)

        val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
        val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
        val textTertiary = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)

        val keyNeutral = if (isDark) Color(0x29334155) else Color(0x66F1F5F9)
        val keyOperator = accent.copy(alpha = 0.25f)
        val keyAction = Color(0x33EF4444)
        val keyEquals = accent

        val blob1 = accent.copy(alpha = if (isAmoled) 0.18f else 0.42f)
        val blob2 = accent.copy(alpha = if (isAmoled) 0.14f else 0.38f)
        val blob3 = accent.copy(alpha = if (isAmoled) 0.12f else 0.32f)

        return GlassColors(
            isDark = isDark,
            accent = accent,
            accentContainer = accent.copy(alpha = 0.25f),
            background = bg,
            surfaceGlass = surfaceGlass,
            surfaceGlassSecondary = surfaceGlassSecondary,
            borderGlass = borderGlass,
            borderGlassHighlight = borderGlassHighlight,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            textTertiary = textTertiary,
            keyNeutral = keyNeutral,
            keyOperator = keyOperator,
            keyAction = keyAction,
            keyEquals = keyEquals,
            blob1 = blob1,
            blob2 = blob2,
            blob3 = blob3
        )
    }

    fun generateGlassPalette(
        themeMode: ThemeMode,
        accent: AccentColor,
        systemDark: Boolean
    ): GlassColors {
        return generateGlassPalette(themeMode, accent.color, systemDark)
    }
}
