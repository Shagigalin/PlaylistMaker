package com.example.playlistmaker.feature_settings.domain.usecase

import com.example.playlistmaker.feature_settings.domain.model.Settings
import com.example.playlistmaker.feature_settings.domain.repository.SettingsRepository

class GetThemeUseCase(
    private val settingsRepository: SettingsRepository
) : GetThemeUseCaseInterface {

    override fun execute(): Settings {
        return settingsRepository.getSettings()
    }
}