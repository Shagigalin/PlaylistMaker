package com.example.playlistmaker.feature_search.domain.repository

import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(query: String): Flow<List<Track>>
}