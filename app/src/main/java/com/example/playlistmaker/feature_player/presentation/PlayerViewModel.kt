package com.example.playlistmaker.feature_player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.usecase.FavoriteTracksUseCase
import com.example.playlistmaker.feature_player.domain.usecase.PlayerControlsUseCase
import com.example.playlistmaker.feature_player.domain.usecase.TimeFormatterUseCase
import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCase
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track?,
    private val playerControlsUseCase: PlayerControlsUseCase,
    private val timeFormatterUseCase: TimeFormatterUseCase,
    private val favoriteTracksUseCase: FavoriteTracksUseCase,
    private val playlistUseCase: PlaylistUseCase
) : ViewModel() {

    companion object {
        private const val UPDATE_INTERVAL_MS = 300L
    }

    private val _state = MutableLiveData(PlayerUiState(track = track))
    val state: LiveData<PlayerUiState> = _state

    private var progressUpdateJob: Job? = null

    init {
        loadPlaylists()

        track?.previewUrl?.let { previewUrl ->
            _state.value = _state.value?.copy(isLoading = true)
            playerControlsUseCase.prepareMediaPlayer(previewUrl)
        }

        observePlayerState()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistUseCase.getAllPlaylists().collect { playlists ->
                _state.value = _state.value?.copy(playlists = playlists)
            }
        }
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            playerControlsUseCase.playerState.collect { playerState ->
                updateUiState(playerState)
            }
        }
    }

    private fun updateUiState(playerState: com.example.playlistmaker.feature_player.domain.model.PlayerState) {
        val currentState = _state.value ?: return

        val currentTime = timeFormatterUseCase.formatTime(playerState.currentPosition)

        _state.value = currentState.copy(
            isPlaying = playerState.isPlaying,
            currentTime = currentTime,
            isLoading = !playerState.isPrepared && currentState.track != null,
            error = playerState.error,
            isPrepared = playerState.isPrepared
        )

        when {
            playerState.isPlaying -> startProgressUpdates()
            !playerState.isPlaying -> stopProgressUpdates()
            playerState.currentPosition == 0 && !playerState.isPlaying -> {
                stopProgressUpdates()
                _state.value = currentState.copy(
                    currentTime = "00:00",
                    isPlaying = false
                )
            }
        }
    }

    fun toggleFavorite() {
        val currentTrack = _state.value?.track ?: return
        viewModelScope.launch {
            if (currentTrack.isFavorite) {
                favoriteTracksUseCase.removeFromFavorites(currentTrack)
                _state.value = _state.value?.copy(
                    track = currentTrack.copy(isFavorite = false)
                )
            } else {
                favoriteTracksUseCase.addToFavorites(currentTrack)
                _state.value = _state.value?.copy(
                    track = currentTrack.copy(isFavorite = true)
                )
            }
        }
    }

    fun togglePlayback() {
        val currentState = _state.value ?: return
        if (currentState.track == null) return

        if (currentState.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        val currentState = _state.value ?: return
        if (currentState.track == null || !currentState.isPrepared) return
        playerControlsUseCase.play()
    }

    fun pause() {
        playerControlsUseCase.pause()
    }

    fun showBottomSheet() {
        _state.value = _state.value?.copy(isBottomSheetVisible = true)
        // Обновляем список плейлистов
        loadPlaylists()
    }

    fun hideBottomSheet() {
        _state.value = _state.value?.copy(isBottomSheetVisible = false)
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        val currentTrack = _state.value?.track ?: return

        viewModelScope.launch {

            _state.value = _state.value?.copy(isLoading = true)

            try {

                val isInPlaylist = playlistUseCase.isTrackInPlaylist(playlist, currentTrack.trackId)

                if (isInPlaylist) {
                    _state.value = _state.value?.copy(
                        addToPlaylistResult = AddToPlaylistResult.AlreadyExists(playlist.name),
                        isLoading = false
                    )
                } else {
                    val result = playlistUseCase.addTrackToPlaylist(playlist, currentTrack)
                    result.fold(
                        onSuccess = {
                            _state.value = _state.value?.copy(
                                addToPlaylistResult = AddToPlaylistResult.Success(playlist.name),
                                isBottomSheetVisible = false,
                                isLoading = false
                            )
                        },
                        onFailure = { error ->
                            _state.value = _state.value?.copy(
                                addToPlaylistResult = AddToPlaylistResult.Error(error.message ?: "Unknown error"),
                                isLoading = false
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value?.copy(
                    addToPlaylistResult = AddToPlaylistResult.Error(e.message ?: "Unknown error"),
                    isLoading = false
                )
            }
        }
    }

    fun onAddToPlaylistResultShown() {
        _state.value = _state.value?.copy(addToPlaylistResult = null)
    }

    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                delay(UPDATE_INTERVAL_MS)
                updateCurrentPosition()
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    private fun updateCurrentPosition() {
        val currentState = _state.value ?: return
        val currentTrack = currentState.track ?: return

        val currentPosition = playerControlsUseCase.getCurrentPosition()
        val currentTime = timeFormatterUseCase.formatTime(currentPosition)

        _state.value = currentState.copy(currentTime = currentTime)
    }

    fun seekTo(progress: Float) {
        val duration = _state.value?.track?.trackTimeMillis?.toInt() ?: return
        val position = (progress * duration).toInt()
        playerControlsUseCase.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        playerControlsUseCase.release()
        stopProgressUpdates()
    }
}