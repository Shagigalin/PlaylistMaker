package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide

class MediaActivity : AppCompatActivity() {

    private lateinit var buttonBack: TextView
    private lateinit var albumCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private lateinit var timerText: TextView
    private lateinit var albumContainer: LinearLayout
    private lateinit var yearContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        setupEdgeToEdge()
        initViews()
        setupBackButton()
        displayTrackInfo()
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.button_back)
        albumCover = findViewById(R.id.album_cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        durationValue = findViewById(R.id.duration_value)
        albumValue = findViewById(R.id.album_value)
        yearValue = findViewById(R.id.year_value)
        genreValue = findViewById(R.id.genre_value)
        countryValue = findViewById(R.id.country_value)
        timerText = findViewById(R.id.timer_text)
        albumContainer = findViewById(R.id.album_container)
        yearContainer = findViewById(R.id.year_container)
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

    private fun setupBackButton() {
        buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Исправлено
        }
    }

    @Suppress("DEPRECATION")
    private fun getTrackFromIntent(): Track? {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("track", Track::class.java)
        } else {
            intent.getSerializableExtra("track") as? Track
        }
    }

    private fun displayTrackInfo() {
        val track = getTrackFromIntent() // Исправлено
        track?.let {
            // Установка основной информации
            trackName.text = it.trackName
            artistName.text = it.artistName
            durationValue.text = it.getFormattedTime()
            timerText.text = it.getFormattedTime()

            // Загрузка обложки
            val artworkUrl = it.getCoverArtwork()

            if (artworkUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(artworkUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(albumCover)
            } else {
                albumCover.setImageResource(R.drawable.placeholder)
            }

            // Отображение альбома (если есть)
            if (!it.collectionName.isNullOrEmpty()) {
                albumValue.text = it.collectionName
                albumContainer.isVisible = true
            } else {
                albumContainer.isVisible = false
            }

            // Отображение года релиза (если есть)
            val releaseYear = it.getReleaseYear()
            if (!releaseYear.isNullOrEmpty()) {
                yearValue.text = releaseYear
                yearContainer.isVisible = true
            } else {
                yearContainer.isVisible = false
            }

            // Отображение жанра
            genreValue.text = it.primaryGenreName ?: getString(R.string.unknown)

            // Отображение страны
            countryValue.text = it.country ?: getString(R.string.unknown)
        }
    }
}