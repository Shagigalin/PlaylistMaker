package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import java.io.IOException

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
    private lateinit var playPauseButton: ImageButton

    private var mediaPlayer: MediaPlayer? = null
    private var progressHandler: Handler? = null
    private var isPlaying = false
    private var isPrepared = false
    private var currentPosition = 0

    private companion object {
        const val UPDATE_INTERVAL = 100L // 100 ms
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        setupEdgeToEdge()
        initViews()
        setupBackButton()
        setupBackPressedCallback()
        setupMediaPlayer()
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
        playPauseButton = findViewById(R.id.btn_play_pause)

        playPauseButton.setOnClickListener {
            if (isPlaying) {
                pausePlayback()
            } else {
                startPlayback()
            }
        }
    }

    private fun setupBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                stopPlayback()
                finish()
            }
        })
    }

    private fun setupBackButton() {
        buttonBack.setOnClickListener {
            stopPlayback()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupMediaPlayer() {
        val track = getTrackFromIntent()
        track?.let {
            // Проверяем наличие previewUrl
            if (it.previewUrl.isNullOrEmpty()) {
                playPauseButton.isVisible = false
                showError("Аудио недоступно для этого трека")
                return
            }

            playPauseButton.isEnabled = false // Отключаем кнопку пока загружается

            mediaPlayer = MediaPlayer().apply {
                setOnPreparedListener { mp ->
                    isPrepared = true
                    playPauseButton.isEnabled = true
                    updateUI()
                    // Автоматически начинаем воспроизведение при загрузке
                    startPlayback()
                }

                setOnCompletionListener {
                    stopPlayback()
                    resetProgress()
                    updateUI()
                }

                setOnErrorListener { mp, what, extra ->
                    playPauseButton.isEnabled = false
                    showError("Ошибка воспроизведения")
                    false
                }

                try {
                    setDataSource(it.previewUrl)
                    prepareAsync() // Асинхронная подготовка
                } catch (e: IOException) {
                    e.printStackTrace()
                    playPauseButton.isEnabled = false
                    showError("Ошибка загрузки трека: ${e.message}")
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                    playPauseButton.isEnabled = false
                    showError("Ошибка состояния плеера")
                }
            }
        } ?: run {
            // Если трек не передан
            playPauseButton.isVisible = false
            showError("Трек не найден")
        }
    }

    private fun startPlayback() {
        if (!isPrepared) return

        mediaPlayer?.let { player ->
            if (currentPosition > 0) {
                player.seekTo(currentPosition)
            }
            player.start()
            isPlaying = true
            startProgressUpdates()
            updateUI()
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        isPlaying = false
        currentPosition = mediaPlayer?.currentPosition ?: 0
        stopProgressUpdates()
        updateUI()
    }

    private fun stopPlayback() {
        mediaPlayer?.stop()
        isPlaying = false
        isPrepared = false
        currentPosition = 0
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        progressHandler = Handler(Looper.getMainLooper())
        progressHandler?.post(object : Runnable {
            override fun run() {
                updateProgress()
                progressHandler?.postDelayed(this, UPDATE_INTERVAL)
            }
        })
    }

    private fun stopProgressUpdates() {
        progressHandler?.removeCallbacksAndMessages(null)
        progressHandler = null
    }

    private fun updateProgress() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                val currentMs = player.currentPosition
                timerText.text = formatTime(currentMs)
            }
        }
    }

    private fun resetProgress() {
        timerText.text = "00:00"
        currentPosition = 0
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun updateUI() {
        if (isPlaying) {
            playPauseButton.setImageResource(R.drawable.pause)
        } else {
            playPauseButton.setImageResource(R.drawable.play)
        }
    }

    private fun showError(message: String) {

        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
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

    @Suppress("DEPRECATION")
    private fun getTrackFromIntent(): Track? {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("track", Track::class.java)
        } else {
            intent.getSerializableExtra("track") as? Track
        }
    }

    private fun displayTrackInfo() {
        val track = getTrackFromIntent()
        track?.let {
            trackName.text = it.trackName
            artistName.text = it.artistName
            durationValue.text = it.getFormattedTime()
            timerText.text = "00:00"

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

            if (!it.collectionName.isNullOrEmpty()) {
                albumValue.text = it.collectionName
                albumContainer.isVisible = true
            } else {
                albumContainer.isVisible = false
            }

            val releaseYear = it.getReleaseYear()
            if (!releaseYear.isNullOrEmpty()) {
                yearValue.text = releaseYear
                yearContainer.isVisible = true
            } else {
                yearContainer.isVisible = false
            }

            genreValue.text = it.primaryGenreName ?: getString(R.string.unknown)
            countryValue.text = it.country ?: getString(R.string.unknown)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            pausePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
        mediaPlayer?.release()
        mediaPlayer = null
        progressHandler?.removeCallbacksAndMessages(null)
    }
}