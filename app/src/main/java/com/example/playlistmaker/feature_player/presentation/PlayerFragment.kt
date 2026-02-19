package com.example.playlistmaker.feature_player.presentation

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.feature_playlist.presentation.adapter.PlaylistBottomSheetAdapter
import com.example.playlistmaker.utils.CustomToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistAdapter: PlaylistBottomSheetAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerBinding.bind(view)

        setupBottomSheet()
        setupViews()
        observeViewModel()

        val track = args.track
        if (track == null) {
            setupEmptyPlayer()
        } else {
            setupPlayerWithTrack()
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            skipCollapsed = true
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                val currentBinding = _binding ?: return

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        currentBinding.overlay.visibility = View.GONE
                        viewModel.hideBottomSheet()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        currentBinding.overlay.visibility = View.VISIBLE
                        currentBinding.overlay.alpha = 0.6f
                    }
                    else -> {
                        currentBinding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                val currentBinding = _binding ?: return


                val alpha = 0.6f * if (slideOffset > 0) slideOffset else 0f
                currentBinding.overlay.alpha = alpha
            }
        })


        playlistAdapter = PlaylistBottomSheetAdapter { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }

        binding.rvPlaylists.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
        }


        binding.btnNewPlaylist.setOnClickListener {

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


            try {

                val action = PlayerFragmentDirections.actionGlobalToCreatePlaylistFragment()
                findNavController().navigate(action)
            } catch (e: Exception) {

                findNavController().navigate(R.id.createPlaylistFragment)
            }
        }
    }

    private fun setupViews() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPlayPause.setOnClickListener {
            viewModel.togglePlayback()
        }

        binding.btnAddToPlaylist.setOnClickListener {
            viewModel.showBottomSheet()
        }

        binding.btnAddToFavorites.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupPlayerWithTrack() {
        enablePlayerControls()
    }

    private fun setupEmptyPlayer() {
        showEmptyPlayerState()
        disablePlayerControls()
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateUI(state)


            if (state.isBottomSheetVisible) {
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

                playlistAdapter.submitList(state.playlists)
            } else {
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }


            state.addToPlaylistResult?.let { result ->
                when (result) {
                    is AddToPlaylistResult.Success -> {
                        showToast("Добавлено в плейлист ${result.playlistName}")

                    }
                    is AddToPlaylistResult.AlreadyExists -> {
                        showToast("Трек уже добавлен в плейлист ${result.playlistName}")

                    }
                    is AddToPlaylistResult.Error -> {
                        showToast("Ошибка: ${result.message}")
                    }
                }
                viewModel.onAddToPlaylistResultShown()
            }
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
        val hasAlbum = !track.collectionName.isNullOrEmpty()
        binding.albumContainer.isVisible = hasAlbum
        binding.albumValue.text = if (hasAlbum) track.collectionName else ""

        val releaseYear = track.getReleaseYear()
        val hasYear = !releaseYear.isNullOrEmpty()
        binding.yearContainer.isVisible = hasYear
        binding.yearValue.text = if (hasYear) releaseYear else ""

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
        CustomToast.show(requireContext(), message)
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