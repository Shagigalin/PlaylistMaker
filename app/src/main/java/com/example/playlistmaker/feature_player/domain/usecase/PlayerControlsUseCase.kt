package com.example.playlistmaker.feature_player.domain.usecase

import android.media.MediaPlayer
import com.example.playlistmaker.feature_player.data.MediaPlayerProvider
import com.example.playlistmaker.feature_player.domain.model.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException

class PlayerControlsUseCase(
    private val mediaPlayerProvider: MediaPlayerProvider) {

    private var mediaPlayer: MediaPlayer? = null
    private val _playerState = MutableStateFlow(PlayerState(isLoading = false))
    val playerState: StateFlow<PlayerState> = _playerState

    fun prepareMediaPlayer(previewUrl: String) {
        mediaPlayer?.release()

        mediaPlayer = mediaPlayerProvider.createMediaPlayer().apply {
            setOnPreparedListener {
                _playerState.update { currentState ->
                    currentState.copy(
                        isPrepared = true,
                        duration = duration
                    )
                }
            }

            setOnCompletionListener {
                _playerState.update { currentState ->
                    currentState.copy(
                        isPlaying = false,
                        currentPosition = 0
                    )
                }
            }

            setOnErrorListener { _, what, extra ->
                _playerState.update { currentState ->
                    currentState.copy(
                        error = "Playback error: $what, $extra",
                        isPlaying = false
                    )
                }
                false
            }

            try {
                setDataSource(previewUrl)
                prepareAsync()

                _playerState.update { currentState ->
                    currentState.copy(
                        isPrepared = false,
                        isLoading = true,
                        error = null
                    )
                }
            } catch (e: IOException) {
                _playerState.update { currentState ->
                    currentState.copy(
                        error = "Failed to load audio: ${e.message}",
                        isPrepared = false,
                        isLoading = false
                    )
                }
            } catch (e: IllegalStateException) {
                _playerState.update { currentState ->
                    currentState.copy(
                        error = "Player in illegal state",
                        isPrepared = false,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun play() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                player.start()
                _playerState.update { currentState ->
                    currentState.copy(
                        isPlaying = true
                    )
                }
            }
        }
    }

    fun pause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                val currentPosition = player.currentPosition
                _playerState.update { currentState ->
                    currentState.copy(
                        isPlaying = false,
                        currentPosition = currentPosition
                    )
                }
            }
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _playerState.update { currentState ->
            currentState.copy(
                currentPosition = position
            )
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        _playerState.value = PlayerState(isLoading = false)
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}