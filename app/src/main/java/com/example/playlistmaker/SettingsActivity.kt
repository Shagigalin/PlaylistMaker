package com.example.playlistmaker

import android.content.ActivityNotFoundException
import android.widget.Toast
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var temaButton: TextView
    private val PREFS_NAME = "theme_prefs"
    private val THEME_KEY = "current_theme"
    private val SUPPORT_EMAIL = "ufa-gazinur@mail.ru"
    private val TERMS_URL = "https://yandex.ru/legal/practicum_offer/ru/"

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

        findViewById<TextView>(R.id.share_button).setOnClickListener {
            shareApp()
        }

        findViewById<TextView>(R.id.help_button).setOnClickListener {
            contactSupport()
        }

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
        val shareText = """
            Привет! Посмотри этот крутой курс по Android-разработке от Практикума:
                        🔗 https://practicum.yandex.ru/android-developer/
                       
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        try {
            startActivity(Intent.createChooser(shareIntent, "Поделиться курсом"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Не найдено приложений для отправки", Toast.LENGTH_SHORT).show()
        }
    }

    private fun contactSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, "Сообщение разработчикам и разработчицам приложения Playlist Maker")
            putExtra(Intent.EXTRA_TEXT, "Спасибо разработчикам и разработчицам за крутое приложение!")
        }

        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "На устройстве не найдено почтового приложения",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openTerms() {
        val termsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_URL))

        try {
            startActivity(termsIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "Браузер не найден",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}