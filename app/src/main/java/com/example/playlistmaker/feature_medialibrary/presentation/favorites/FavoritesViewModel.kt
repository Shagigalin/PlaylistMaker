package com.example.playlistmaker.feature_medialibrary.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.usecase.FavoriteTracksUseCase
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteTracksUseCase: FavoriteTracksUseCase
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            favoriteTracksUseCase.getAllFavorites().collect { tracks ->
                _state.value = if (tracks.isEmpty()) {
                    FavoritesState.Empty
                } else {
                    FavoritesState.Content(tracks = tracks)
                }
            }
        }
    }
}

sealed class FavoritesState {
    object Empty : FavoritesState()
    data class Content(val tracks: List<Track>) : FavoritesState()
}