package com.sae.wavetime.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "wave_time_prefs")

object AuthKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
}

object SettingKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

class AppDataStore(
    private val context: Context
) {
    val accessTokenFlow: Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[AuthKeys.ACCESS_TOKEN]
        }

    val themeModeFlow: Flow<ThemeMode> =
        context.dataStore.data.map { prefs ->
            when (prefs[SettingKeys.THEME_MODE]) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
        }

    @Volatile
    var cachedAccessToken: String? = null
        private set

    init {
        CoroutineScope(Dispatchers.IO).launch {
            accessTokenFlow.collect { token ->
                cachedAccessToken = token
            }
        }
    }

    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[AuthKeys.ACCESS_TOKEN] = token
        }
    }

    suspend fun clearAccessToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(AuthKeys.ACCESS_TOKEN)
        }
    }

    suspend fun saveThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[SettingKeys.THEME_MODE] = when (mode) {
                ThemeMode.SYSTEM -> "system"
                ThemeMode.LIGHT -> "light"
                ThemeMode.DARK -> "dark"
            }
        }
    }
}