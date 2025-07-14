package com.example.playlistmaker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var buttonSearch: Button
    private lateinit var buttonMedia: Button
    private lateinit var buttonSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        buttonSearch = findViewById(R.id.button_search)
        buttonMedia = findViewById(R.id.button_media)
        buttonSettings = findViewById(R.id.button_settings)

        buttonSearch.setOnClickListener {
            Toast.makeText(this, "Кнопка Поиск нажата", Toast.LENGTH_SHORT).show()
        }

        buttonMedia.setOnClickListener {
            Toast.makeText(this, "Кнопка Медиатека нажата", Toast.LENGTH_SHORT).show()
        }

        buttonSettings.setOnClickListener {
            Toast.makeText(this, "Кнопка Настройки нажата", Toast.LENGTH_SHORT).show()
        }

    }
}