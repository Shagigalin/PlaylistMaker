package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    fun getAllFavorites(): Flow<List<Track>>
    suspend fun getFavoriteIds(): List<Long>
    suspend fun isFavorite(trackId: Long): Boolean
}