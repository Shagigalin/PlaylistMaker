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
    private val track: Track?,
    private val playerControlsUseCase: PlayerControlsUseCase,
    private val timeFormatterUseCase: TimeFormatterUseCase
) : ViewModel() {

    private val _state = MutableLiveData<PlayerUiState>(PlayerUiState(track = track))
    val state: LiveData<PlayerUiState> = _state

    private var updateProgressJob: Job? = null
    private val UPDATE_INTERVAL = 100L

    init {
        // Установите загрузку если есть previewUrl
        _state.value = _state.value?.copy(
            isLoading = track?.previewUrl != null
        )

        // Подготовьте медиаплеер
        track?.previewUrl?.let { previewUrl ->
            playerControlsUseCase.prepareMediaPlayer(previewUrl)
        }

        observePlayerState()
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
        val currentTrack = currentState.track

        val currentTime = timeFormatterUseCase.formatTime(playerState.currentPosition)
        val progress = timeFormatterUseCase.formatProgress(
            playerState.currentPosition,
            currentTrack?.trackTimeMillis?.toInt() ?: 30000
        )

        _state.value = currentState.copy(
            isPlaying = playerState.isPlaying,
            currentTime = currentTime,

            isLoading = !playerState.isPrepared && currentTrack != null,
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

        )
    }

    override fun onCleared() {
        super.onCleared()
        playerControlsUseCase.release()
        stopProgressUpdates()
    }
}