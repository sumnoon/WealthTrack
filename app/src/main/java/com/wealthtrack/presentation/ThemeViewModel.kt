package com.wealthtrack.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wealthtrack.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val themeMode: StateFlow<String> = userPreferences.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "AUTO"
        )

    var currentTheme by mutableStateOf("AUTO")
        private set

    init {
        viewModelScope.launch {
            themeMode.collect { mode ->
                currentTheme = mode
            }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            userPreferences.saveThemeMode(mode)
        }
        currentTheme = mode
    }

    fun getThemeDescription(): String {
        return when (currentTheme) {
            "LIGHT" -> "Light theme enabled"
            "DARK" -> "Dark theme enabled"
            "AUTO" -> "Auto theme (follows system)"
            else -> "Auto theme (follows system)"
        }
    }
}
