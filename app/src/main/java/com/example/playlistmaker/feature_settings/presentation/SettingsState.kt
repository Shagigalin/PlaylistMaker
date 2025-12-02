package com.example.playlistmaker.feature_settings.presentation

data class SettingsState(
    val isDarkTheme: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)