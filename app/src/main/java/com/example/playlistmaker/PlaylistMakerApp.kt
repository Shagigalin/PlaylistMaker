package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.di.*
import com.example.playlistmaker.domain.di.useCaseModule
import com.example.playlistmaker.feature_medialibrary.di.mediaLibraryModule
import com.example.playlistmaker.feature_playlist.di.playlistModule
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
                databaseModule,
                repositoryModule,
                useCaseModule,
                playlistModule,
                searchModule,
                playerModule,
                mediaLibraryModule,
                settingsModule)
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