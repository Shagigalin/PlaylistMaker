package com.example.playlistmaker.data.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.feature_search.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.feature_settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.feature_settings.domain.repository.SettingsRepository
import com.example.playlistmaker.feature_settings.domain.usecase.GetThemeUseCase
import com.example.playlistmaker.feature_settings.domain.usecase.GetThemeUseCaseInterface
import com.example.playlistmaker.feature_settings.domain.usecase.SetThemeUseCase
import com.example.playlistmaker.feature_settings.domain.usecase.SetThemeUseCaseInterface
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.google.gson.Gson

val appModule = module {

    single { Gson() }


    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }


    single<SharedPreferencesStorage> {
        val sharedPreferences = androidContext()
            .getSharedPreferences("search_history", Context.MODE_PRIVATE)
        SharedPreferencesStorage(
            sharedPreferences = sharedPreferences
        )
    }


    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPreferences = get())
    }


    factory<GetThemeUseCaseInterface> {
        GetThemeUseCase(settingsRepository = get())
    }


    factory<SetThemeUseCaseInterface> {
        SetThemeUseCase(settingsRepository = get())
    }
}