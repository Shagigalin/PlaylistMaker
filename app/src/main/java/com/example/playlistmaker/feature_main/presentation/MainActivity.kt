package com.example.playlistmaker.feature_main.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.feature_settings.domain.model.ThemeSettings
import com.example.playlistmaker.feature_settings.domain.usecase.GetThemeUseCaseInterface
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val getThemeUseCase: GetThemeUseCaseInterface by inject()

    override fun onCreate(savedInstanceState: Bundle?) {

        applySavedTheme()

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBarInsets()
        setupNavigation()
    }

    private fun applySavedTheme() {
        val theme = getThemeUseCase.execute()
        val isDarkTheme = theme == ThemeSettings.DARK
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun setupStatusBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.navHostFragment) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBarInsets.top)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigationView) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(bottom = navigationBarInsets.bottom)
            insets
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerFragment, R.id.createPlaylistFragment -> {
                    binding.bottomNavigationView.visibility = android.view.View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = android.view.View.VISIBLE
                }
            }
        }
    }
}