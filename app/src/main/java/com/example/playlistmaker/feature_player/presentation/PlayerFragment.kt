package com.example.playlistmaker.feature_player.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val args: PlayerFragmentArgs by navArgs()


    private lateinit var currentTrack: Track

    private val viewModel: PlayerViewModel by viewModel(parameters = {
        parametersOf(args.track)
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerBinding.bind(view)

        // Проверяем трек один раз при создании
        if (false) {
            setupEmptyPlayer()
            return
        }

        // Сохраняем трек в переменную
        currentTrack = args.track!!
        setupPlayerWithTrack()
    }

    private fun setupPlayerWithTrack() {
        // Настройка с треком
        setupViews()
        setupTrackInfo(currentTrack)
        observeViewModel()
        enablePlayerControls()
    }

    private fun setupEmptyPlayer() {
        // Настройка пустого плеера
        showEmptyPlayerState()
        setupEmptyControls()
    }

    private fun setupViews() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPlayPause.setOnClickListener {
            viewModel.togglePlayback()
        }

        binding.btnAddToPlaylist.setOnClickListener {
            showToast("Добавить в плейлист")
        }

        binding.btnAddToFavorites.setOnClickListener {
            showToast("Добавить в избранное")
        }
    }

    private fun setupEmptyControls() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Устанавливаем сообщения для кнопок
        binding.btnPlayPause.setOnClickListener {
            showToast("Выберите трек в поиске")
        }

        binding.btnAddToPlaylist.setOnClickListener {
            showToast("Выберите трек в поиске")
        }

        binding.btnAddToFavorites.setOnClickListener {
            showToast("Выберите трек в поиске")
        }

        // Отключаем кнопки
        disablePlayerControls()
    }

    private fun setupTrackInfo(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.durationValue.text = track.getFormattedTime()

        setupAlbumCover(track)
        setupTrackDetails(track)

        binding.currentTime.isVisible = true
    }

    private fun setupAlbumCover(track: Track) {
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
    }

    private fun setupTrackDetails(track: Track) {
        // Альбом
        val hasAlbum = !track.collectionName.isNullOrEmpty()
        binding.albumContainer.isVisible = hasAlbum
        binding.albumValue.text = if (hasAlbum) track.collectionName else ""

        // Год
        val releaseYear = track.getReleaseYear()
        val hasYear = !releaseYear.isNullOrEmpty()
        binding.yearContainer.isVisible = hasYear
        binding.yearValue.text = if (hasYear) releaseYear else ""

        // Жанр и страна
        binding.genreValue.text = track.primaryGenreName ?: getString(R.string.unknown)
        binding.countryValue.text = track.country ?: getString(R.string.unknown)
    }

    private fun showEmptyPlayerState() {
        binding.trackName.text = "Нет трека"
        binding.artistName.text = "Выберите трек в поиске"
        binding.durationValue.text = "0:00"
        binding.currentTime.text = "0:00"

        binding.albumCover.setImageResource(R.drawable.placeholder)

        binding.albumContainer.isVisible = false
        binding.yearContainer.isVisible = false
        binding.genreValue.text = "-"
        binding.countryValue.text = "-"

        binding.loadingProgressBar.isVisible = false
    }

    private fun enablePlayerControls() {
        binding.btnPlayPause.isEnabled = true
        binding.btnAddToPlaylist.isEnabled = true
        binding.btnAddToFavorites.isEnabled = true
    }

    private fun disablePlayerControls() {
        binding.btnPlayPause.isEnabled = false
        binding.btnAddToPlaylist.isEnabled = false
        binding.btnAddToFavorites.isEnabled = false
        binding.btnPlayPause.setImageResource(R.drawable.play)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.observe(viewLifecycleOwner) { state ->
                updateUI(state)
            }
        }
    }

    private fun updateUI(state: PlayerUiState) {
        // Обновляем только динамические элементы
        updatePlaybackState(state)
        handleErrors(state)
    }

    private fun updatePlaybackState(state: PlayerUiState) {
        val playPauseIcon = if (state.isPlaying) R.drawable.pause else R.drawable.play
        binding.btnPlayPause.setImageResource(playPauseIcon)

        binding.currentTime.text = state.currentTime
        binding.loadingProgressBar.isVisible = state.isLoading

        // Кнопка доступна только если плеер готов
        binding.btnPlayPause.isEnabled = state.isPrepared
    }

    private fun handleErrors(state: PlayerUiState) {
        state.error?.let { error ->
            showToast(error)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}