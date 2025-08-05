package com.example.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var sharedPrefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val THEME_KEY = "current_theme"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Инициализация SharedPreferences
        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Применение темы перед созданием активности
        applyTheme(sharedPrefs.getBoolean(THEME_KEY, false))

        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setupToolbar()
        setupThemeSwitch()
    }

    private fun initViews() {
        themeSwitch = binding.themeSwitch
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun setupThemeSwitch() {
        themeSwitch.isChecked = sharedPrefs.getBoolean(THEME_KEY, false)
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit { putBoolean(THEME_KEY, isChecked) }
            applyTheme(isChecked)
            recreate()
        }

        // Настройка обработчиков кликов
        binding.shareButton.setOnClickListener { shareApp() }
        binding.helpButton.setOnClickListener { contactSupport() }
        binding.feedbackButton.setOnClickListener { openTerms() }
    }

    private fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun shareApp() {
        try {
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.share_course_text, getString(R.string.android_course_url)))
                },
                getString(R.string.share_dialog_title)
            ).also { startActivity(it) }
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.no_apps_found)
        }
    }

    private fun contactSupport() {
        try {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            }.also { startActivity(it) }
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.no_email_client)
        }
    }

    private fun openTerms() {
        try {
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_url)))
                .also { startActivity(it) }
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.no_browser_found)
        }
    }

    private fun showToast(messageRes: Int) {
        Toast.makeText(this, getString(messageRes), Toast.LENGTH_SHORT).show()
    }
}