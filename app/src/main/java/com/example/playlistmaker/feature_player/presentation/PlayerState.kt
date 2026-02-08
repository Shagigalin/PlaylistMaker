package com.example.playlistmaker.feature_player.presentation

import com.example.playlistmaker.feature_search.domain.model.Track

data class PlayerUiState(
    val track: Track? = null,
    val isPlaying: Boolean = false,
    val currentTime: String = "00:00",
    val isLoading: Boolean = false,

    val error: String? = null,
    val isPrepared: Boolean = false
)