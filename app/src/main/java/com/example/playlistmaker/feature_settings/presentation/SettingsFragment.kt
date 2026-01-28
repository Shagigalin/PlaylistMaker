package com.example.playlistmaker.feature_settings.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {


        binding.temaButton.setOnClickListener {
            viewModel.toggleTheme()
        }

        binding.shareButton.setOnClickListener { shareApp() }
        binding.helpButton.setOnClickListener { contactSupport() }
        binding.strelRButton.setOnClickListener { openTerms() }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.observe(viewLifecycleOwner) { state ->
                updateUI(state)
            }
        }
    }

    private fun updateUI(state: SettingsState) {
        val iconRes = if (state.isDarkTheme) R.drawable.on else R.drawable.off
        binding.temaButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            context?.getDrawable(iconRes),
            null
        )
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
        Toast.makeText(context, getString(messageRes), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}