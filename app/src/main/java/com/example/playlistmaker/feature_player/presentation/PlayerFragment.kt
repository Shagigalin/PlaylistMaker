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
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val args: PlayerFragmentArgs by navArgs()

    private val viewModel: PlayerViewModel by viewModel(parameters = {
        parametersOf(args.track)
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerBinding.bind(view)


        val track = args.track
        if (track == null) {
            setupEmptyPlayer()
            return
        }

        setupPlayerWithTrack()
    }

    private fun setupPlayerWithTrack() {
        setupViews()
        observeViewModel()
        enablePlayerControls()
    }

    private fun setupEmptyPlayer() {
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
            viewModel.toggleFavorite()
        }
    }

    private fun setupEmptyControls() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPlayPause.setOnClickListener {
            showToast("Выберите трек в поиске")
        }

        binding.btnAddToPlaylist.setOnClickListener {
            showToast("Выберите трек в поиске")
        }

        binding.btnAddToFavorites.setOnClickListener {
            showToast("Выберите трек в поиске")
        }

        disablePlayerControls()
    }

    private fun observeViewModel() {

        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: PlayerUiState) {
        val track = state.track ?: return

        updateTrackInfo(track)
        updatePlaybackState(state)
        updateFavoriteButton(track)
        handleErrors(state)
    }

    private fun updateTrackInfo(track: com.example.playlistmaker.feature_search.domain.model.Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.durationValue.text = track.getFormattedTime()

        setupAlbumCover(track)
        setupTrackDetails(track)

        binding.currentTime.isVisible = true
    }

    private fun setupAlbumCover(track: com.example.playlistmaker.feature_search.domain.model.Track) {
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

    private fun setupTrackDetails(track: com.example.playlistmaker.feature_search.domain.model.Track) {
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

    private fun updatePlaybackState(state: PlayerUiState) {
        val playPauseIcon = if (state.isPlaying) R.drawable.pause else R.drawable.play
        binding.btnPlayPause.setImageResource(playPauseIcon)

        binding.currentTime.text = state.currentTime
        binding.loadingProgressBar.isVisible = state.isLoading

        binding.btnPlayPause.isEnabled = state.isPrepared
    }

    private fun updateFavoriteButton(track: com.example.playlistmaker.feature_search.domain.model.Track) {
        val favoriteIcon = if (track.isFavorite) {
            R.drawable.like_active
        } else {
            R.drawable.izbran
        }
        binding.btnAddToFavorites.setImageResource(favoriteIcon)
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