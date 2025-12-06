package com.example.playlistmaker.feature_player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.playlistmaker.feature_player.domain.usecase.PlayerControlsUseCase
import com.example.playlistmaker.feature_player.domain.usecase.TimeFormatterUseCase
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerControlsUseCase: PlayerControlsUseCase,
    private val timeFormatterUseCase: TimeFormatterUseCase
) : ViewModel() {

    private val _state = MutableLiveData<PlayerUiState>(PlayerUiState())
    val state: LiveData<PlayerUiState> = _state

    private var updateProgressJob: Job? = null
    private val UPDATE_INTERVAL = 100L

    init {
        observePlayerState()
    }

    fun setTrack(track: Track?) {
        _state.value = _state.value?.copy(
            track = track,
            isLoading = track != null && track.previewUrl != null
        )

        track?.previewUrl?.let { previewUrl ->
            playerControlsUseCase.prepareMediaPlayer(previewUrl)
        }
    }

    fun togglePlayback() {
        if (_state.value?.isPlaying == true) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        playerControlsUseCase.play()
    }

    fun pause() {
        playerControlsUseCase.pause()
        stopProgressUpdates()
    }

    fun seekTo(progress: Float) {
        val duration = _state.value?.track?.trackTimeMillis?.toInt() ?: 30000
        val position = (progress * duration).toInt()
        playerControlsUseCase.seekTo(position)
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            playerControlsUseCase.playerState.collect { playerState ->
                updateUiState(playerState)
            }
        }
    }

    private fun updateUiState(playerState: com.example.playlistmaker.feature_player.domain.model.PlayerState) {
        val currentState = _state.value ?: PlayerUiState()
        val track = currentState.track


        val currentTime = timeFormatterUseCase.formatTime(playerState.currentPosition)
        val progress = timeFormatterUseCase.formatProgress(
            playerState.currentPosition,
            track?.trackTimeMillis?.toInt() ?: 30000
        )

        _state.value = currentState.copy(
            isPlaying = playerState.isPlaying,
            currentTime = currentTime,

            progress = progress,
            isLoading = !playerState.isPrepared && track != null,
            error = playerState.error,
            isPrepared = playerState.isPrepared
        )

        if (playerState.isPlaying) {
            startProgressUpdates()
        } else {
            stopProgressUpdates()
        }
    }

    private fun startProgressUpdates() {
        updateProgressJob?.cancel()
        updateProgressJob = viewModelScope.launch {
            while (true) {
                delay(UPDATE_INTERVAL)
                updateCurrentPosition()
            }
        }
    }

    private fun stopProgressUpdates() {
        updateProgressJob?.cancel()
        updateProgressJob = null
    }

    private fun updateCurrentPosition() {
        val currentPosition = playerControlsUseCase.getCurrentPosition()
        val currentState = _state.value ?: return

        val currentTime = timeFormatterUseCase.formatTime(currentPosition)
        val progress = timeFormatterUseCase.formatProgress(
            currentPosition,
            currentState.track?.trackTimeMillis?.toInt() ?: 30000
        )

        _state.value = currentState.copy(
            currentTime = currentTime,
            progress = progress
        )
    }

    override fun onCleared() {
        super.onCleared()
        playerControlsUseCase.release()
        stopProgressUpdates()
    }
}