package com.example.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.store.SettingsStore

interface NavigatorVibrateApi {
    fun vibrate(durationMs: Long): Boolean
    fun vibrate(pattern: LongArray): Boolean
    fun cancel(): Boolean
}

class AndroidNavigatorVibrateApi(private val context: Context) : NavigatorVibrateApi {
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override fun vibrate(durationMs: Long): Boolean {
        return try {
            val vib = vibrator ?: return false
            if (!vib.hasVibrator()) return false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(durationMs)
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    override fun vibrate(pattern: LongArray): Boolean {
        return try {
            val vib = vibrator ?: return false
            if (!vib.hasVibrator()) return false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(pattern, -1)
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    override fun cancel(): Boolean {
        return try {
            vibrator?.cancel()
            true
        } catch (_: Exception) {
            false
        }
    }
}

class HapticFeedbackService(
    private val context: Context,
    private val settingsStore: SettingsStore? = null,
    private val vibrateApi: NavigatorVibrateApi = AndroidNavigatorVibrateApi(context)
) {
    val isHapticActive: Boolean
        get() = settingsStore?.hapticsEnabled ?: true

    fun toggleHaptics() {
        settingsStore?.let {
            it.updateHapticsEnabled(!it.hapticsEnabled)
        }
    }

    fun updateHapticState(enabled: Boolean) {
        settingsStore?.updateHapticsEnabled(enabled)
    }

    fun performKeyTap(durationMs: Long = 8L): Boolean {
        if (!isHapticActive) return false
        return vibrateApi.vibrate(durationMs)
    }
}

val LocalHapticFeedbackService = staticCompositionLocalOf<HapticFeedbackService?> { null }
