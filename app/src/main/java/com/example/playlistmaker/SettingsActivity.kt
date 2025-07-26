package com.example.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
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

        // Кнопка "Назад"
        findViewById<TextView>(R.id.button_back).setOnClickListener {
            onBackPressed()
        }

        // Кнопка темы
        temaButton = findViewById(R.id.tema_button)
        updateButtonUI(isDarkTheme)
        temaButton.setOnClickListener {
            toggleTheme()
        }

        // Кнопка "Поделиться"
        findViewById<TextView>(R.id.share_button).setOnClickListener {
            shareApp()
        }

        // Кнопка "Написать в поддержку"
        findViewById<TextView>(R.id.help_button).setOnClickListener {
            contactSupport()
        }

        // Кнопка "Пользовательское соглашение"
        findViewById<TextView>(R.id.strel_r_button).setOnClickListener {
            openTerms()
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

    private fun shareApp() {
        val shareText = getString(R.string.share_course_text, getString(R.string.android_course_url))

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        try {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_dialog_title)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.no_apps_found), Toast.LENGTH_SHORT).show()
        }
    }

    private fun contactSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
        }

        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.no_email_client),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openTerms() {
        val termsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_url)))

        try {
            startActivity(termsIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.no_browser_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}