package com.example.playlistmaker.feature_settings.domain.usecase

import com.example.playlistmaker.feature_settings.domain.model.Settings
import com.example.playlistmaker.feature_settings.domain.repository.SettingsRepository

class SetThemeUseCase(
    private val settingsRepository: SettingsRepository
) : SetThemeUseCaseInterface {

    override fun execute(settings: Settings) {
        settingsRepository.saveSettings(settings)
    }
}