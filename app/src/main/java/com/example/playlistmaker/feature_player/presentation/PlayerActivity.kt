package com.example.playlistmaker.feature_player.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.feature_player.di.PlayerComponent
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.launch

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private val viewModel: PlayerViewModel by viewModels { PlayerComponent.viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupViews()
        observeViewModel()
        loadTrack()
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

        // Кнопка Play/Pause
        binding.btnPlayPause.setOnClickListener {
            viewModel.togglePlayback()
        }

        // Кнопка "Добавить в плейлист"
        binding.btnAddToPlaylist.setOnClickListener {
            Toast.makeText(this, "Добавить в плейлист", Toast.LENGTH_SHORT).show()
        }

        // Кнопка "Добавить в избранное"
        binding.btnAddToFavorites.setOnClickListener {
            Toast.makeText(this, "Добавить в избранное", Toast.LENGTH_SHORT).show()
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
        // Отображаем информацию о треке
        state.track?.let { track ->
            binding.trackName.text = track.trackName
            binding.artistName.text = track.artistName
            binding.durationValue.text = track.getFormattedTime()

            // Используем currentTime для отображения текущего времени воспроизведения
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

        // Активируем/деактивируем кнопки
        val isEnabled = state.isPrepared
        binding.btnPlayPause.isEnabled = isEnabled

        // Убедимся что currentTime виден
        binding.currentTime.isVisible = true

        // УДАЛИТЕ ЭТУ СТРОКУ - totalTime больше не существует
        // binding.totalTime.isVisible = false
    }

    private fun loadTrack() {
        val track = getTrackFromIntent()
        track?.let {
            viewModel.setTrack(it)
        } ?: run {
            Toast.makeText(this, "Трек не найден", Toast.LENGTH_SHORT).show()
            finish()
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

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // ViewModel сам очистит ресурсы в onCleared()
    }
}