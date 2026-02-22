package com.example.playlistmaker.feature_main.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.core.NavigationController
import com.example.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationController {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->

            val hideOnDestinations = setOf(
                R.id.playlistDetailsFragment,
                R.id.playerFragment,
                R.id.createPlaylistFragment,
                R.id.editPlaylistFragment
            )

            val shouldHide = destination.id in hideOnDestinations
            showBottomNavigation(!shouldHide)
        }
    }

    override fun showBottomNavigation(show: Boolean) {
        if (show) {
            binding.bottomNavigationView.visibility = View.VISIBLE

            binding.navHostFragment.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToTop = R.id.bottom_navigation_view
            }
        } else {
            binding.bottomNavigationView.visibility = View.GONE

            binding.navHostFragment.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }
    }
}