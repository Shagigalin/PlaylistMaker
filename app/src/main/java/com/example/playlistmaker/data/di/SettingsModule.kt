// SettingsModule.kt - ИСПРАВЛЕННЫЙ
package com.example.playlistmaker.data.di

import com.example.playlistmaker.feature_settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.feature_settings.domain.repository.SettingsRepository
import com.example.playlistmaker.feature_settings.domain.usecase.GetThemeUseCase
import com.example.playlistmaker.feature_settings.domain.usecase.GetThemeUseCaseInterface
import com.example.playlistmaker.feature_settings.domain.usecase.SetThemeUseCase
import com.example.playlistmaker.feature_settings.domain.usecase.SetThemeUseCaseInterface
import com.example.playlistmaker.feature_settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {

    // Repository
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }


    factory<GetThemeUseCaseInterface> {
        GetThemeUseCase(get())
    }

    factory<SetThemeUseCaseInterface> {
        SetThemeUseCase(get())
    }

    // ViewModel
    viewModel {
        SettingsViewModel(
            getThemeUseCase = get(),
            setThemeUseCase = get()
        )
    }
}