package com.example.playlistmaker.presentation.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

open class BaseActivity : AppCompatActivity() {

    protected lateinit var sharedPrefs: SharedPreferences

    companion object {
        const val PREFS_NAME = "theme_prefs"
        const val THEME_KEY = "current_theme"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Инициализация SharedPreferences
        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Применение темы перед созданием активности
        applyTheme(sharedPrefs.getBoolean(THEME_KEY, false))

        super.onCreate(savedInstanceState)
    }

    protected fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    protected fun isDarkTheme(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }
}