package com.example.playlistmaker.feature_player.presentation

import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_search.domain.model.Track

data class PlayerUiState(
    val track: Track? = null,
    val isPlaying: Boolean = false,
    val currentTime: String = "00:00",
    val isLoading: Boolean = false,
    val isLoadingPlaylists: Boolean = false,
    val isPrepared: Boolean = false,
    val error: String? = null,
    val playlists: List<Playlist> = emptyList(),
    val isBottomSheetVisible: Boolean = false,
    val addToPlaylistResult: AddToPlaylistResult? = null
)

sealed class AddToPlaylistResult {
    data class Success(val playlistName: String) : AddToPlaylistResult()
    data class AlreadyExists(val playlistName: String) : AddToPlaylistResult()
    data class Error(val message: String) : AddToPlaylistResult()
}