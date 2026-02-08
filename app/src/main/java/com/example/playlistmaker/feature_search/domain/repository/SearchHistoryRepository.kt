package com.example.playlistmaker.feature_search.domain.repository

import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getSearchHistory(): Flow<List<Track>>
    suspend fun addToHistory(track: Track)
    suspend fun clearHistory()
}