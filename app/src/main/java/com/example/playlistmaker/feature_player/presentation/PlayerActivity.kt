package com.example.playlistmaker.feature_player.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding

    // Исправленный вызов ViewModel
    private val viewModel: PlayerViewModel by viewModel(parameters = {
        val track = getTrackFromIntent()

        parametersOf(track)
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupViews()
        observeViewModel()

        // Проверяем есть ли трек и показываем соответствующее состояние
        if (getTrackFromIntent() == null) {
            showEmptyPlayer()
        }
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
        // Кнопка назад
        binding.buttonBack.setOnClickListener {
            finish()
        }

        // Кнопка Play/Pause - работает только если есть трек
        binding.btnPlayPause.setOnClickListener {
            if (getTrackFromIntent() != null) {
                viewModel.togglePlayback()
            } else {
                Toast.makeText(this, "Выберите трек в поиске", Toast.LENGTH_SHORT).show()
            }
        }

        // Кнопка "Добавить в плейлист"
        binding.btnAddToPlaylist.setOnClickListener {
            if (getTrackFromIntent() != null) {
                Toast.makeText(this, "Добавить в плейлист", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Выберите трек в поиске", Toast.LENGTH_SHORT).show()
            }
        }

        // Кнопка "Добавить в избранное"
        binding.btnAddToFavorites.setOnClickListener {
            if (getTrackFromIntent() != null) {
                Toast.makeText(this, "Добавить в избранное", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Выберите трек в поиске", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.observe(this@PlayerActivity) { state ->
                updateUI(state)
            }
        }
    }

    private fun updateUI(state: PlayerUiState) {
        val track = getTrackFromIntent()

        if (track != null && state.track != null) {
            // Показываем информацию о треке
            showTrackInfo(state)
        } else {
            // Показываем пустой плейер
            showEmptyPlayer()
        }

        // Кнопка Play/Pause
        val playPauseIcon = if (state.isPlaying) R.drawable.pause else R.drawable.play
        binding.btnPlayPause.setImageResource(playPauseIcon)

        // Показываем/скрываем загрузку
        binding.loadingProgressBar.isVisible = state.isLoading

        // Показываем ошибку если есть
        state.error?.let { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        // Включаем/выключаем кнопку Play/Pause
        binding.btnPlayPause.isEnabled = state.isPrepared && (track != null)

        binding.currentTime.isVisible = true
    }

    private fun showTrackInfo(state: PlayerUiState) {
        state.track?.let { track ->
            binding.trackName.text = track.trackName
            binding.artistName.text = track.artistName
            binding.durationValue.text = track.getFormattedTime()
            binding.currentTime.text = state.currentTime

            // Обложка
            val artworkUrl = track.getCoverArtwork()
            if (artworkUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(artworkUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.albumCover)
            } else {
                binding.albumCover.setImageResource(R.drawable.placeholder)
            }

            // Альбом
            if (!track.collectionName.isNullOrEmpty()) {
                binding.albumValue.text = track.collectionName
                binding.albumContainer.isVisible = true
            } else {
                binding.albumContainer.isVisible = false
            }

            // Год релиза
            val releaseYear = track.getReleaseYear()
            if (!releaseYear.isNullOrEmpty()) {
                binding.yearValue.text = releaseYear
                binding.yearContainer.isVisible = true
            } else {
                binding.yearContainer.isVisible = false
            }

            // Жанр
            binding.genreValue.text = track.primaryGenreName ?: getString(R.string.unknown)

            // Страна
            binding.countryValue.text = track.country ?: getString(R.string.unknown)

            // Включаем кнопки управления
            binding.btnPlayPause.isEnabled = true
            binding.btnAddToPlaylist.isEnabled = true
            binding.btnAddToFavorites.isEnabled = true
        }
    }

    private fun showEmptyPlayer() {
        // Настраиваем UI для пустого плейера
        binding.trackName.text = "Нет трека"
        binding.artistName.text = "Выберите трек в поиске"
        binding.durationValue.text = "0:00"
        binding.currentTime.text = "0:00"

        // Заглушка для обложки
        binding.albumCover.setImageResource(R.drawable.placeholder)

        // Скрываем детали
        binding.albumContainer.isVisible = false
        binding.yearContainer.isVisible = false
        binding.genreValue.text = "-"
        binding.countryValue.text = "-"

        // Отключаем кнопки управления
        binding.btnPlayPause.isEnabled = false
        binding.btnAddToPlaylist.isEnabled = false
        binding.btnAddToFavorites.isEnabled = false

        // Меняем иконку play/pause на play (но disabled)
        binding.btnPlayPause.setImageResource(R.drawable.play)

        // Скрываем прогресс-бар
        binding.loadingProgressBar.isVisible = false
    }

    @Suppress("DEPRECATION")
    private fun getTrackFromIntent(): Track? {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("track", Track::class.java)
        } else {
            intent.getSerializableExtra("track") as? Track
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Освобождаем ресурсы уже в ViewModel
    }
}