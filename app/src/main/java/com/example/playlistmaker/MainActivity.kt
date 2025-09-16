package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.Toast
import androidx.core.view.updatePadding

class MainActivity : BaseActivity() {

    private lateinit var buttonSearch: Button
    private lateinit var buttonMedia: Button
    private lateinit var buttonSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupEdgeToEdge()

        buttonSearch = findViewById(R.id.button_search)
        buttonMedia = findViewById(R.id.button_media)
        buttonSettings = findViewById(R.id.button_settings)

        buttonSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        buttonMedia.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
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
}