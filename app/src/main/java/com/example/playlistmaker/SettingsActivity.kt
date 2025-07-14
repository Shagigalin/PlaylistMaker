package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {

    private lateinit var buttonBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        buttonBack = findViewById(R.id.button_back)
        buttonBack.setOnClickListener {
            finish()
        }

    }


}