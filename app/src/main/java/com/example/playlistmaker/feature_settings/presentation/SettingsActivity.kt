package com.example.playlistmaker.feature_settings.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.feature_settings.di.SettingsComponent
import com.example.playlistmaker.presentation.activity.BaseActivity
import kotlinx.coroutines.launch

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels {
        SettingsComponent.createViewModelFactory(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupViews()
        observeViewModel()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
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
        binding.buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.temaButton.setOnClickListener {
            viewModel.toggleTheme()
        }

        binding.shareButton.setOnClickListener { shareApp() }
        binding.helpButton.setOnClickListener { contactSupport() }
        binding.strelRButton.setOnClickListener { openTerms() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.observe(this@SettingsActivity) { state ->
                updateUI(state)
            }
        }
    }

    private fun updateUI(state: SettingsState) {

        val iconRes = if (state.isDarkTheme) R.drawable.on else R.drawable.off
        binding.temaButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            getDrawable(iconRes),
            null
        )


        applyTheme(state.isDarkTheme)


        state.error?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_course_text, getString(R.string.android_course_url)))
            }

            val chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_dialog_title))
            startActivity(chooserIntent)
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.no_apps_found)
        }
    }

    private fun contactSupport() {
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")

                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            }
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.no_email_client)
        }
    }

    private fun openTerms() {
        try {
            val termsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_url)))
            startActivity(termsIntent)
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.no_browser_found)
        }
    }

    private fun showToast(messageRes: Int) {
        Toast.makeText(this, getString(messageRes), Toast.LENGTH_SHORT).show()
    }
}