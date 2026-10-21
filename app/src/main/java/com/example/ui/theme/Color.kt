package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Gcalc Material Accent Palette (24+ colors)
enum class AccentColor(val displayName: String, val color: Color, val darkBlob: Color) {
    CYAN("Cyan", Color(0xFF06B6D4), Color(0xFF0E7490)),
    BLUE("Blue", Color(0xFF3B82F6), Color(0xFF1D4ED8)),
    INDIGO("Indigo", Color(0xFF6366F1), Color(0xFF4338CA)),
    PURPLE("Purple", Color(0xFFA855F7), Color(0xFF7E22CE)),
    DEEP_PURPLE("Deep Purple", Color(0xFF7C3AED), Color(0xFF5B21B6)),
    PINK("Pink", Color(0xFFEC4899), Color(0xFFBE185D)),
    RED("Red", Color(0xFFEF4444), Color(0xFFB91C1C)),
    DEEP_ORANGE("Deep Orange", Color(0xFFFF5722), Color(0xFFC2410C)),
    ORANGE("Orange", Color(0xFFF97316), Color(0xFFC2410C)),
    AMBER("Amber", Color(0xFFF59E0B), Color(0xFFB45309)),
    LIME("Lime", Color(0xFFEAB308), Color(0xFFA16207)),
    LIGHT_GREEN("Light Green", Color(0xFF84CC16), Color(0xFF4D7C0F)),
    GREEN("Green", Color(0xFF10B981), Color(0xFF047857)),
    TEAL("Teal", Color(0xFF14B8A6), Color(0xFF0F766E)),
    LIGHT_BLUE("Light Blue", Color(0xFF0EA5E9), Color(0xFF0369A1)),
    BROWN("Brown", Color(0xFF8D6E63), Color(0xFF5D4037)),
    BLUE_GREY("Blue Grey", Color(0xFF64748B), Color(0xFF334155)),
    VIOLET("Violet", Color(0xFF8B5CF6), Color(0xFF6D28D9)),
    SLATE("Slate", Color(0xFF475569), Color(0xFF1E293B)),
    EMERALD("Emerald", Color(0xFF10B981), Color(0xFF059669)),
    YELLOW("Yellow", Color(0xFFFACC15), Color(0xFFCA8A04)),
    ROSE("Rose", Color(0xFFF43F5E), Color(0xFFBE123C)),
    SKY("Sky", Color(0xFF0EA5E9), Color(0xFF0369A1)),
    CUSTOM("Custom", Color(0xFF06B6D4), Color(0xFF0E7490))
}

// Predefined Glass Themes
enum class GlassThemePreset(
    val title: String,
    val accent: AccentColor,
    val mode: ThemeMode
) {
    ARCTIC_BLUE("Arctic Blue", AccentColor.LIGHT_BLUE, ThemeMode.DARK),
    SUNSET_ORANGE("Sunset Orange", AccentColor.DEEP_ORANGE, ThemeMode.DARK),
    MIDNIGHT_VIOLET("Midnight Violet", AccentColor.VIOLET, ThemeMode.DARK),
    NEON_CYAN("Neon Cyan", AccentColor.CYAN, ThemeMode.AMOLED),
    EMERALD_MINT("Emerald Mint", AccentColor.TEAL, ThemeMode.DARK),
    ROSE_GOLD("Rose Gold", AccentColor.PINK, ThemeMode.LIGHT)
}
