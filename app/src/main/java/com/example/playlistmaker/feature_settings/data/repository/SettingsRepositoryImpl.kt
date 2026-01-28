package com.example.playlistmaker.feature_settings.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.feature_settings.domain.model.Settings
import com.example.playlistmaker.feature_settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : com.example.playlistmaker.feature_settings.domain.repository.SettingsRepository {

    companion object {
        private const val DARK_THEME_KEY = "dark_theme_key"
    }

    override fun getSettings(): Settings {
        val isDarkTheme = sharedPreferences.getBoolean(DARK_THEME_KEY, false)
        val theme = if (isDarkTheme) ThemeSettings.DARK else ThemeSettings.LIGHT
        return Settings(theme = theme)
    }

    override fun saveSettings(settings: Settings) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, settings.theme == ThemeSettings.DARK)
            .apply()
    }
}