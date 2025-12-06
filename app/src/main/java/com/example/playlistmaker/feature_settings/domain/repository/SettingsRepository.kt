package com.example.playlistmaker.feature_settings.domain.repository

import com.example.playlistmaker.feature_settings.domain.model.Settings

interface SettingsRepository {
    fun getSettings(): Settings
    fun saveSettings(settings: Settings)
}