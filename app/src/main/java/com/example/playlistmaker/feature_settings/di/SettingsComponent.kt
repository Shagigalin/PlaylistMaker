package com.example.playlistmaker.feature_settings.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.feature_settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.feature_settings.domain.repository.SettingsRepository
import com.example.playlistmaker.feature_settings.domain.usecase.GetThemeUseCase
import com.example.playlistmaker.feature_settings.domain.usecase.SetThemeUseCase
import com.example.playlistmaker.feature_settings.presentation.SettingsViewModel

object SettingsComponent {

    private fun provideSettingsRepository(context: Context): SettingsRepository {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return SettingsRepositoryImpl(sharedPreferences)
    }

    val viewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val context = com.example.playlistmaker.PlaylistMakerApp.applicationContext()

            val repository = provideSettingsRepository(context)
            val getThemeUseCase = GetThemeUseCase(repository)
            val setThemeUseCase = SetThemeUseCase(repository)

            return SettingsViewModel(getThemeUseCase, setThemeUseCase) as T
        }
    }
}