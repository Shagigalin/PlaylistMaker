package com.example.playlistmaker.feature_settings.domain.usecase

import com.example.playlistmaker.feature_settings.domain.model.Settings
import com.example.playlistmaker.feature_settings.domain.model.ThemeSettings

interface GetThemeUseCaseInterface {
    fun execute(): ThemeSettings
}