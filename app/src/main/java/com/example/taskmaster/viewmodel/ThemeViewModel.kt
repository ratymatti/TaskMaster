package com.example.taskmaster.viewmodel

import android.app.Application
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmaster.data.preferences.ThemeMode
import com.example.taskmaster.data.preferences.ThemePreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing theme state across the application.
 */
class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val themePreferenceManager = ThemePreferenceManager(application)

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    init {
        // Collect theme preference on initialization
        viewModelScope.launch {
            themePreferenceManager.themeMode.collect { mode ->
                _themeMode.value = mode
            }
        }
    }

    /**
     * Update the theme mode and persist the preference.
     *
     * @param mode The new theme mode to apply
     */
    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferenceManager.setThemeMode(mode)
        }
    }

    /**
     * Determine if dark theme should be used based on theme mode and system setting.
     * This should be called from a Composable context to access system theme.
     *
     * @param isSystemInDarkTheme Current system dark theme setting
     * @return true if dark theme should be applied
     */
    fun shouldUseDarkTheme(isSystemInDarkTheme: Boolean): Boolean {
        return when (_themeMode.value) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme
        }
    }
}

