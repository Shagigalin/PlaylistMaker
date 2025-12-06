package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PlaylistMakerApp : Application() {

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val THEME_KEY = "dark_theme"

        lateinit var instance: PlaylistMakerApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Инициализация Koin
        startKoin {
            androidContext(this@PlaylistMakerApp)
            modules(
                appModule,
                mainModule,
                searchModule,
                playerModule,
                settingsModule
            )
        }

        // Применение темы
        applyThemeOnStart()
    }

    private fun applyThemeOnStart() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean(THEME_KEY, false)

        val themeMode = if (isDarkTheme) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
}