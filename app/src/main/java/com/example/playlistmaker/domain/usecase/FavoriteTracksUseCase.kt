package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksUseCase {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    fun getAllFavorites(): Flow<List<Track>>
    suspend fun toggleFavorite(track: Track): Boolean
}