package com.example.playlistmaker.feature_player.domain.model

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0,
    val isPrepared: Boolean = false,
    val error: String? = null
)