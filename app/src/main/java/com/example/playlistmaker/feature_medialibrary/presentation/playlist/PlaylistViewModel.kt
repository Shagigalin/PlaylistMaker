package com.example.playlistmaker.feature_medialibrary.presentation.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCase
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistUseCase: PlaylistUseCase
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistState>(PlaylistState.Loading)
    val state: LiveData<PlaylistState> = _state

    init {
        observePlaylists()
    }

    fun refreshPlaylists() {
        loadPlaylists()
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            playlistUseCase.getAllPlaylists().collect { playlists ->

                _state.value = when {
                    playlists.isEmpty() -> PlaylistState.Empty
                    else -> PlaylistState.Content(playlists)
                }
            }
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            try {
                playlistUseCase.getAllPlaylists().collect { playlists ->
                    _state.value = if (playlists.isEmpty()) {
                        PlaylistState.Empty
                    } else {
                        PlaylistState.Content(playlists)
                    }
                }
            } catch (e: Exception) {
                _state.value = PlaylistState.Error(e.message ?: "Unknown error")
            }
        }
    }


}

sealed class PlaylistState {
    object Loading : PlaylistState()
    object Empty : PlaylistState()
    data class Content(val playlists: List<Playlist>) : PlaylistState()
    data class Error(val message: String) : PlaylistState()
}