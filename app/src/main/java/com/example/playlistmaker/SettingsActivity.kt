package com.example.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Включаем Edge-to-Edge ДО setContentView()
        enableEdgeToEdge()
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

            view.updatePadding(
                top = statusBarInsets.top,
                bottom = navigationBarInsets.bottom
            )

            insets
        }
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