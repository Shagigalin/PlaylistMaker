package com.example.playlistmaker.feature_player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.usecase.FavoriteTracksUseCase
import com.example.playlistmaker.feature_player.domain.usecase.PlayerControlsUseCase
import com.example.playlistmaker.feature_player.domain.usecase.TimeFormatterUseCase
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track?,
    private val playerControlsUseCase: PlayerControlsUseCase,
    private val timeFormatterUseCase: TimeFormatterUseCase,
    private val favoriteTracksUseCase: FavoriteTracksUseCase
) : ViewModel() {

    companion object {
        private const val UPDATE_INTERVAL_MS = 300L
    }

    private val _state = MutableLiveData<PlayerUiState>()
    val state: LiveData<PlayerUiState> = _state

    private var progressUpdateJob: Job? = null

    init {
        _state.value = PlayerUiState(
            track = track,
            isPrepared = track?.previewUrl == null
        )

        track?.previewUrl?.let { previewUrl ->
            _state.value = _state.value?.copy(isLoading = true)
            playerControlsUseCase.prepareMediaPlayer(previewUrl)
        }

        observePlayerState()
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
                try {
                    favoriteTracksUseCase.removeFromFavorites(currentTrack)
                    _state.value = _state.value?.copy(
                        track = currentTrack.copy(isFavorite = false)
                    )
                } catch (e: Exception) {
                    _state.value = _state.value?.copy(
                        error = requireContext().getString(R.string.error_removing_from_favorites)
                    )
                }
            } else {
                try {
                    favoriteTracksUseCase.addToFavorites(currentTrack)
                    _state.value = _state.value?.copy(
                        track = currentTrack.copy(isFavorite = true)
                    )
                } catch (e: Exception) {
                    _state.value = _state.value?.copy(
                        error = requireContext().getString(R.string.error_adding_to_favorites)
                    )
                }
            }
        }
    }

    private fun requireContext(): android.content.Context {
        return android.app.Application().applicationContext
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