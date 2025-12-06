package com.example.playlistmaker.feature_settings.domain.usecase

import com.example.playlistmaker.feature_settings.domain.model.Settings

interface GetThemeUseCaseInterface {
    fun execute(): Settings
}