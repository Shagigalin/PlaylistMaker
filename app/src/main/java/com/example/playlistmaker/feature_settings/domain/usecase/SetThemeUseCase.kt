package com.example.playlistmaker.feature_settings.domain.usecase

import com.example.playlistmaker.feature_settings.domain.model.ThemeSettings
import com.example.playlistmaker.feature_settings.domain.repository.SettingsRepository

class SetThemeUseCase(
    private val settingsRepository: SettingsRepository
) : SetThemeUseCaseInterface {

    override fun execute(theme: ThemeSettings) {

        val isDarkTheme = theme == ThemeSettings.DARK
        val settings = com.example.playlistmaker.feature_settings.domain.model.Settings(
            theme = theme
        )
        settingsRepository.saveSettings(settings)
    }
}