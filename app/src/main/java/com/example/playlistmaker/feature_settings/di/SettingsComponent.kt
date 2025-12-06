package com.example.playlistmaker.feature_settings.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.feature_settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.feature_settings.domain.usecase.GetThemeUseCase
import com.example.playlistmaker.feature_settings.domain.usecase.SetThemeUseCase
import com.example.playlistmaker.feature_settings.presentation.SettingsViewModel

object SettingsComponent {

    fun createViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                val repository = SettingsRepositoryImpl(sharedPreferences)

                val getThemeUseCase = GetThemeUseCase(repository)
                val setThemeUseCase = SetThemeUseCase(repository)

                return SettingsViewModel(getThemeUseCase, setThemeUseCase) as T
            }
        }
    }
}