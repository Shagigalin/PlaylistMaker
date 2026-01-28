package com.example.playlistmaker.feature_settings.domain.usecase

import com.example.playlistmaker.feature_settings.domain.model.ThemeSettings

interface SetThemeUseCaseInterface {
    fun execute(theme: ThemeSettings)
}