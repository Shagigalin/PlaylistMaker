package com.example.playlistmaker.feature_settings.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.feature_settings.domain.model.Settings
import com.example.playlistmaker.feature_settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    companion object {
        private const val THEME_KEY = "dark_theme"
    }

    override fun getSettings(): Settings {
        val isDarkTheme = sharedPreferences.getBoolean(THEME_KEY, false)
        return Settings(isDarkTheme)
    }

    override fun saveSettings(settings: Settings) {
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, settings.isDarkTheme)
            .apply()
    }
}