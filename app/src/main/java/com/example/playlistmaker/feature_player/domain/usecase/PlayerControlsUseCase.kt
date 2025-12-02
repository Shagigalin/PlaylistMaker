package com.example.playlistmaker.feature_player.domain.usecase

import android.media.MediaPlayer
import com.example.playlistmaker.feature_player.domain.model.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

class PlayerControlsUseCase {

    private var mediaPlayer: MediaPlayer? = null
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState

    fun prepareMediaPlayer(previewUrl: String) {
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener {
                _playerState.value = _playerState.value.copy(
                    isPrepared = true,
                    duration = duration
                )
            }

            setOnCompletionListener {
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    currentPosition = 0
                )
            }

            setOnErrorListener { _, what, extra ->
                _playerState.value = _playerState.value.copy(
                    error = "Playback error: $what, $extra",
                    isPlaying = false
                )
                false
            }

            try {
                setDataSource(previewUrl)
                prepareAsync()
            } catch (e: IOException) {
                _playerState.value = _playerState.value.copy(
                    error = "Failed to load audio: ${e.message}",
                    isPrepared = false
                )
            } catch (e: IllegalStateException) {
                _playerState.value = _playerState.value.copy(
                    error = "Player in illegal state",
                    isPrepared = false
                )
            }
        }
    }

    fun play() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                player.start()
                _playerState.value = _playerState.value.copy(
                    isPlaying = true
                )
            }
        }
    }

    fun pause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                val currentPosition = player.currentPosition
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    currentPosition = currentPosition
                )
            }
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _playerState.value = _playerState.value.copy(
            currentPosition = position
        )
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        _playerState.value = PlayerState()
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}