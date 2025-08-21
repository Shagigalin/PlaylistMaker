package com.example.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.view.updateLayoutParams
import android.view.ViewGroup
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPrefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val THEME_KEY = "current_theme"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Включаем Edge-to-Edge ДО setContentView()
        enableEdgeToEdge()

        // Инициализация SharedPreferences
        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Применение темы перед созданием активности
        applyTheme(sharedPrefs.getBoolean(THEME_KEY, false))

        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настраиваем обработку системных инсетов
        setupEdgeToEdge()

        setupViews()
        updateThemeIcon()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // Обновляем отступы для корневого View
            view.updatePadding(
                top = statusBarInsets.top,
                bottom = navigationBarInsets.bottom
            )

            insets
        }

        // Настраиваем кнопку назад для Edge-to-Edge

    }



    // Функция для конвертации dp в px
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun setupViews() {
        // Кнопка назад
        binding.buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Переключение темы
        binding.temaButton.setOnClickListener {
            val newThemeState = !sharedPrefs.getBoolean(THEME_KEY, false)
            sharedPrefs.edit { putBoolean(THEME_KEY, newThemeState) }
            applyTheme(newThemeState)
            updateThemeIcon()
            recreate()
        }

        // Поделиться приложением
        binding.shareButton.setOnClickListener { shareApp() }

        // Справка
        binding.helpButton.setOnClickListener { contactSupport() }

        // Условия использования
        binding.strelRButton.setOnClickListener { openTerms() }
    }

    private fun updateThemeIcon() {
        val isDarkTheme = sharedPrefs.getBoolean(THEME_KEY, false)
        val iconRes = if (isDarkTheme) R.drawable.on else R.drawable.off
        binding.temaButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            ContextCompat.getDrawable(this, iconRes),
            null
        )
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