package com.example.playlistmaker

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var temaButton: TextView
    private val PREFS_NAME = "theme_prefs"
    private val THEME_KEY = "current_theme"

    override fun onCreate(savedInstanceState: Bundle?) {


        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isDarkTheme = prefs.getBoolean(THEME_KEY, false)
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<TextView>(R.id.button_back).setOnClickListener {
            onBackPressed()
        }


        temaButton = findViewById(R.id.tema_button)


        updateButtonUI(isDarkTheme)


        temaButton.setOnClickListener {
            toggleTheme()
        }
    }

    private fun toggleTheme() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isDarkTheme = prefs.getBoolean(THEME_KEY, false)

        if (isDarkTheme) {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            prefs.edit().putBoolean(THEME_KEY, false).apply()
            updateButtonUI(false)
        } else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            prefs.edit().putBoolean(THEME_KEY, true).apply()
            updateButtonUI(true)
        }


        recreate()
    }

    private fun updateButtonUI(isDarkModeEnabled: Boolean) {
        if (isDarkModeEnabled) {
            temaButton.text = getString(R.string.tema_dark)
            temaButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.on, 0)
        } else {
            temaButton.text = getString(R.string.tema_light)
            temaButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.off, 0)
        }
    }
}