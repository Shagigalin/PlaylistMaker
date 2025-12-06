package com.example.playlistmaker.presentation.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

abstract class BaseActivity : AppCompatActivity() {

    protected val sharedPrefs: SharedPreferences by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    protected fun applyTheme(isDarkTheme: Boolean) {
        println("DEBUG: Applying theme - dark: $isDarkTheme")
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }



    companion object {
        const val THEME_KEY = "dark_theme"
    }
}