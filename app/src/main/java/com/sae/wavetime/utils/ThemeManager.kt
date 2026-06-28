package com.sae.wavetime.utils

import androidx.appcompat.app.AppCompatDelegate
import com.sae.wavetime.local.ThemeMode

object ThemeManager {

    fun applyTheme(mode: ThemeMode) {
        val nightMode = when (mode) {
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        }

        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}