package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class PlaylistMakerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        applyThemeOnStart()
    }

    private fun applyThemeOnStart() {
        val sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)

        val themeMode = if (isDarkTheme) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
}