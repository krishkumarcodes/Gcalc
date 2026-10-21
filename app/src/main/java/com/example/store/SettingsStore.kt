package com.example.store

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.engine.AngleMode
import com.example.engine.NumberFormatMode
import com.example.ui.theme.AccentColor
import com.example.ui.theme.ThemeMode
import org.json.JSONObject

class SettingsStore(context: Context) {

    private val prefs: SharedPreferences = run {
        migrateFromLegacySettings(context)
        context.getSharedPreferences("gcalc_settings", Context.MODE_PRIVATE)
    }

    var hapticsEnabled by mutableStateOf(prefs.getBoolean(KEY_HAPTICS, true))
        private set

    var soundEnabled by mutableStateOf(prefs.getBoolean(KEY_SOUND, false))
        private set

    var themeMode by mutableStateOf(
        try {
            ThemeMode.valueOf(prefs.getString(KEY_THEME, ThemeMode.DARK.name) ?: ThemeMode.DARK.name)
        } catch (_: Exception) {
            ThemeMode.DARK
        }
    )
        private set

    var accentColor by mutableStateOf(
        try {
            AccentColor.valueOf(prefs.getString(KEY_ACCENT, AccentColor.CYAN.name) ?: AccentColor.CYAN.name)
        } catch (_: Exception) {
            AccentColor.CYAN
        }
    )
        private set

    var customAccentColorHex by mutableStateOf(prefs.getString(KEY_CUSTOM_ACCENT, "#06B6D4") ?: "#06B6D4")
        private set

    var angleMode by mutableStateOf(
        try {
            AngleMode.valueOf(prefs.getString(KEY_ANGLE_MODE, AngleMode.DEG.name) ?: AngleMode.DEG.name)
        } catch (_: Exception) {
            AngleMode.DEG
        }
    )
        private set

    var numberFormatMode by mutableStateOf(
        try {
            NumberFormatMode.valueOf(prefs.getString(KEY_NUMBER_FORMAT, NumberFormatMode.AUTO.name) ?: NumberFormatMode.AUTO.name)
        } catch (_: Exception) {
            NumberFormatMode.AUTO
        }
    )
        private set

    var decimalPlaces by mutableStateOf(prefs.getInt(KEY_DECIMAL_PLACES, 10))
        private set

    var glassIntensity by mutableStateOf(prefs.getFloat(KEY_GLASS_INTENSITY, 0.75f))
        private set

    var thousandsSeparator by mutableStateOf(prefs.getBoolean(KEY_THOUSANDS, true))
        private set

    var wideZero by mutableStateOf(prefs.getBoolean(KEY_WIDE_ZERO, false))
        private set

    var isLeftHanded by mutableStateOf(prefs.getBoolean(KEY_LEFT_HANDED, false))
        private set

    var animateBg by mutableStateOf(prefs.getBoolean(KEY_ANIMATE_BG, true))
        private set

    var keepScreenAwake by mutableStateOf(prefs.getBoolean(KEY_KEEP_AWAKE, false))
        private set

    var exponentStyle by mutableStateOf(prefs.getString(KEY_EXP_STYLE, "SUPERSCRIPT") ?: "SUPERSCRIPT")
        private set

    var showWorkingDefault by mutableStateOf(prefs.getBoolean(KEY_SHOW_WORKING, true))
        private set

    // Memory Store
    var memoryValue by mutableDoubleStateOf(
        prefs.getFloat(KEY_MEMORY_VAL, 0f).toDouble()
    )
        private set

    var recentlyUsedTools by mutableStateOf(
        prefs.getString(KEY_RECENT_TOOLS, "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    )
        private set

    fun updateRecentTool(toolId: String) {
        val current = recentlyUsedTools.toMutableList()
        current.remove(toolId)
        current.add(0, toolId)
        val updated = current.take(5)
        recentlyUsedTools = updated
        prefs.edit().putString(KEY_RECENT_TOOLS, updated.joinToString(",")).apply()
    }

    val variables = mutableStateMapOf<String, Double>()

    init {
        listOf("A", "B", "C", "D", "E", "F").forEach { varName ->
            if (prefs.contains("key_var_$varName")) {
                variables[varName] = prefs.getFloat("key_var_$varName", 0f).toDouble()
            }
        }
    }

    fun toggleHaptics() {
        updateHapticsEnabled(!hapticsEnabled)
    }

    fun updateHapticsEnabled(enabled: Boolean) {
        hapticsEnabled = enabled
        prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
    }

    fun updateSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
    }

    fun updateThemeMode(mode: ThemeMode) {
        themeMode = mode
        prefs.edit().putString(KEY_THEME, mode.name).apply()
    }

    fun updateAccentColor(accent: AccentColor) {
        accentColor = accent
        prefs.edit().putString(KEY_ACCENT, accent.name).apply()
    }

    fun updateCustomAccentColorHex(hex: String) {
        customAccentColorHex = hex
        prefs.edit().putString(KEY_CUSTOM_ACCENT, hex).apply()
        accentColor = AccentColor.CUSTOM
        prefs.edit().putString(KEY_ACCENT, AccentColor.CUSTOM.name).apply()
    }

    fun updateAngleMode(mode: AngleMode) {
        angleMode = mode
        prefs.edit().putString(KEY_ANGLE_MODE, mode.name).apply()
    }

    fun updateNumberFormatMode(format: NumberFormatMode) {
        numberFormatMode = format
        prefs.edit().putString(KEY_NUMBER_FORMAT, format.name).apply()
    }

    fun updateDecimalPlaces(places: Int) {
        decimalPlaces = places
        prefs.edit().putInt(KEY_DECIMAL_PLACES, places).apply()
    }

    fun updateGlassIntensity(intensity: Float) {
        glassIntensity = intensity
        prefs.edit().putFloat(KEY_GLASS_INTENSITY, intensity).apply()
    }

    fun updateThousandsSeparator(enabled: Boolean) {
        thousandsSeparator = enabled
        prefs.edit().putBoolean(KEY_THOUSANDS, enabled).apply()
    }

    fun updateWideZero(enabled: Boolean) {
        wideZero = enabled
        prefs.edit().putBoolean(KEY_WIDE_ZERO, enabled).apply()
    }

    fun updateLeftHanded(enabled: Boolean) {
        isLeftHanded = enabled
        prefs.edit().putBoolean(KEY_LEFT_HANDED, enabled).apply()
    }

    fun updateAnimateBg(enabled: Boolean) {
        animateBg = enabled
        prefs.edit().putBoolean(KEY_ANIMATE_BG, enabled).apply()
    }

    fun updateKeepScreenAwake(enabled: Boolean) {
        keepScreenAwake = enabled
        prefs.edit().putBoolean(KEY_KEEP_AWAKE, enabled).apply()
    }

    fun updateExponentStyle(style: String) {
        exponentStyle = style
        prefs.edit().putString(KEY_EXP_STYLE, style).apply()
    }

    fun updateShowWorkingDefault(enabled: Boolean) {
        showWorkingDefault = enabled
        prefs.edit().putBoolean(KEY_SHOW_WORKING, enabled).apply()
    }

    fun updateMemoryValue(value: Double) {
        memoryValue = value
        prefs.edit().putFloat(KEY_MEMORY_VAL, value.toFloat()).apply()
    }

    fun setVariable(name: String, value: Double) {
        variables[name] = value
        prefs.edit().putFloat("key_var_$name", value.toFloat()).apply()
    }

    fun exportSettingsJson(): String {
        val obj = JSONObject()
        obj.put("hapticsEnabled", hapticsEnabled)
        obj.put("soundEnabled", soundEnabled)
        obj.put("themeMode", themeMode.name)
        obj.put("accentColor", accentColor.name)
        obj.put("angleMode", angleMode.name)
        obj.put("numberFormatMode", numberFormatMode.name)
        obj.put("decimalPlaces", decimalPlaces)
        obj.put("glassIntensity", glassIntensity)
        obj.put("thousandsSeparator", thousandsSeparator)
        obj.put("wideZero", wideZero)
        obj.put("isLeftHanded", isLeftHanded)
        obj.put("animateBg", animateBg)
        obj.put("keepScreenAwake", keepScreenAwake)
        obj.put("exponentStyle", exponentStyle)
        obj.put("showWorkingDefault", showWorkingDefault)
        return obj.toString(2)
    }

    fun importSettingsJson(jsonStr: String): Boolean {
        return try {
            val obj = JSONObject(jsonStr)
            if (obj.has("hapticsEnabled")) updateHapticsEnabled(obj.getBoolean("hapticsEnabled"))
            if (obj.has("soundEnabled")) updateSoundEnabled(obj.getBoolean("soundEnabled"))
            if (obj.has("themeMode")) updateThemeMode(ThemeMode.valueOf(obj.getString("themeMode")))
            if (obj.has("accentColor")) updateAccentColor(AccentColor.valueOf(obj.getString("accentColor")))
            if (obj.has("angleMode")) updateAngleMode(AngleMode.valueOf(obj.getString("angleMode")))
            if (obj.has("numberFormatMode")) updateNumberFormatMode(NumberFormatMode.valueOf(obj.getString("numberFormatMode")))
            if (obj.has("decimalPlaces")) updateDecimalPlaces(obj.getInt("decimalPlaces"))
            if (obj.has("glassIntensity")) updateGlassIntensity(obj.getDouble("glassIntensity").toFloat())
            if (obj.has("thousandsSeparator")) updateThousandsSeparator(obj.getBoolean("thousandsSeparator"))
            if (obj.has("wideZero")) updateWideZero(obj.getBoolean("wideZero"))
            if (obj.has("isLeftHanded")) updateLeftHanded(obj.getBoolean("isLeftHanded"))
            if (obj.has("animateBg")) updateAnimateBg(obj.getBoolean("animateBg"))
            if (obj.has("keepScreenAwake")) updateKeepScreenAwake(obj.getBoolean("keepScreenAwake"))
            if (obj.has("exponentStyle")) updateExponentStyle(obj.getString("exponentStyle"))
            if (obj.has("showWorkingDefault")) updateShowWorkingDefault(obj.getBoolean("showWorkingDefault"))
            true
        } catch (_: Exception) {
            false
        }
    }

    fun resetSettingsToDefaults() {
        updateHapticsEnabled(true)
        updateSoundEnabled(false)
        updateThemeMode(ThemeMode.DARK)
        updateAccentColor(AccentColor.CYAN)
        updateAngleMode(AngleMode.DEG)
        updateNumberFormatMode(NumberFormatMode.AUTO)
        updateDecimalPlaces(10)
        updateGlassIntensity(0.75f)
        updateThousandsSeparator(true)
        updateWideZero(false)
        updateLeftHanded(false)
        updateAnimateBg(true)
        updateKeepScreenAwake(false)
        updateExponentStyle("SUPERSCRIPT")
        updateShowWorkingDefault(true)
    }

    fun eraseEverything() {
        resetSettingsToDefaults()
        updateMemoryValue(0.0)
        variables.clear()
        prefs.edit().clear().apply()
    }

    companion object {
        private fun migrateFromLegacySettings(context: Context) {
            try {
                val oldPrefs = context.getSharedPreferences("glasscalc_settings", Context.MODE_PRIVATE)
                val newPrefs = context.getSharedPreferences("gcalc_settings", Context.MODE_PRIVATE)
                val allOld = oldPrefs.all
                if (allOld.isNotEmpty()) {
                    val editor = newPrefs.edit()
                    for ((key, value) in allOld) {
                        if (!newPrefs.contains(key)) {
                            when (value) {
                                is Boolean -> editor.putBoolean(key, value)
                                is String -> editor.putString(key, value)
                                is Int -> editor.putInt(key, value)
                                is Float -> editor.putFloat(key, value)
                                is Long -> editor.putLong(key, value)
                            }
                        }
                    }
                    editor.apply()
                    oldPrefs.edit().clear().apply()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private const val KEY_HAPTICS = "key_haptics_enabled"
        private const val KEY_SOUND = "key_sound_enabled"
        private const val KEY_THEME = "key_theme_mode"
        private const val KEY_ACCENT = "key_accent_color"
        private const val KEY_CUSTOM_ACCENT = "key_custom_accent_color"
        private const val KEY_ANGLE_MODE = "key_angle_mode"
        private const val KEY_NUMBER_FORMAT = "key_number_format"
        private const val KEY_DECIMAL_PLACES = "key_decimal_places"
        private const val KEY_GLASS_INTENSITY = "key_glass_intensity"
        private const val KEY_THOUSANDS = "key_thousands_separator"
        private const val KEY_WIDE_ZERO = "key_wide_zero"
        private const val KEY_LEFT_HANDED = "key_left_handed"
        private const val KEY_ANIMATE_BG = "key_animate_bg"
        private const val KEY_KEEP_AWAKE = "key_keep_awake"
        private const val KEY_EXP_STYLE = "key_exp_style"
        private const val KEY_SHOW_WORKING = "key_show_working"
        private const val KEY_MEMORY_VAL = "key_memory_val"
        private const val KEY_RECENT_TOOLS = "key_recent_tools"
    }
}

val LocalSettingsStore = staticCompositionLocalOf<SettingsStore> {
    error("No SettingsStore provided")
}
