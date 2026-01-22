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

    // Инициализация состояния с переданным треком
    private val _state = MutableLiveData<PlayerUiState>(
        PlayerUiState(
            track = track,
            isPrepared = track?.previewUrl == null // Если нет previewUrl - плеер сразу готов
        )
    )

    val state: LiveData<PlayerUiState> = _state

    private var updateProgressJob: Job? = null
    private val UPDATE_INTERVAL = 100L // Обновление каждые 100мс

    init {
        // Инициализируем только если есть трек с previewUrl
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
        val currentTrack = currentState.track

        val currentTime = timeFormatterUseCase.formatTime(playerState.currentPosition)

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

    // Управление воспроизведением
    fun togglePlayback() {
        val currentState = _state.value ?: return

        // Если нет трека - ничего не делаем
        if (currentState.track == null) return

        if (currentState.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        val currentState = _state.value ?: return

        // Проверяем, готов ли плеер и есть ли трек
        if (currentState.track == null || !currentState.isPrepared) return

        playerControlsUseCase.play()
    }

    fun pause() {
        playerControlsUseCase.pause()
        stopProgressUpdates()
    }

    // Управление прогрессом
    fun seekTo(progress: Float) {
        val duration = _state.value?.track?.trackTimeMillis?.toInt() ?: return
        val position = (progress * duration).toInt()
        playerControlsUseCase.seekTo(position)
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
        val currentState = _state.value ?: return
        val currentTrack = currentState.track ?: return

        val currentPosition = playerControlsUseCase.getCurrentPosition()
        val currentTime = timeFormatterUseCase.formatTime(currentPosition)

        _state.value = currentState.copy(currentTime = currentTime)
    }

    // Очистка ресурсов
    override fun onCleared() {
        super.onCleared()
        playerControlsUseCase.release()
        stopProgressUpdates()
    }
}

