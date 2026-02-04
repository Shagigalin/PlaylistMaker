package com.example.playlistmaker.feature_settings.domain.usecase

import com.example.playlistmaker.feature_settings.domain.model.Settings
import com.example.playlistmaker.feature_settings.domain.model.ThemeSettings
import com.example.playlistmaker.feature_settings.domain.repository.SettingsRepository

class GetThemeUseCase(
    private val settingsRepository: SettingsRepository
) : GetThemeUseCaseInterface {

    override fun execute(): ThemeSettings {
        val settings = settingsRepository.getSettings()
        return settings.theme
    }
}