package com.wealthtrack.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

// Extension to create DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * User preferences manager using DataStore
 * Stores user settings like theme preference persistently
 */
class UserPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_mode")
        private val APP_LOCK_TIMEOUT_KEY = longPreferencesKey("app_lock_timeout_seconds")
        private val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey("biometric_enabled")
    }

    // Theme preference: "LIGHT", "DARK", or "AUTO"
    val themeMode: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[THEME_KEY] ?: "AUTO"
        }

    // App lock timeout in seconds (0 = immediate, -1 = disabled)
    val appLockTimeoutSeconds: Flow<Long> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[APP_LOCK_TIMEOUT_KEY] ?: 300L // Default 5 minutes
        }

    // Whether biometric is enabled
    val biometricEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[BIOMETRIC_ENABLED_KEY] ?: true
        }

    // Save theme preference
    suspend fun saveThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode
        }
    }

    // Save app lock timeout
    suspend fun saveAppLockTimeout(seconds: Long) {
        context.dataStore.edit { preferences ->
            preferences[APP_LOCK_TIMEOUT_KEY] = seconds
        }
    }

    // Save biometric enabled setting
    suspend fun saveBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED_KEY] = enabled
        }
    }
}
