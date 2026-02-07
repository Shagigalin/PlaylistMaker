package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksUseCaseImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteTracksUseCase {

    override suspend fun addToFavorites(track: Track) {
        repository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: Track) {
        repository.removeFromFavorites(track)
    }

    override fun getAllFavorites(): Flow<List<Track>> {
        return repository.getAllFavorites()
    }

    override suspend fun toggleFavorite(track: Track): Boolean {
        return if (track.isFavorite) {
            removeFromFavorites(track)
            false
        } else {
            addToFavorites(track)
            true
        }
    }
}