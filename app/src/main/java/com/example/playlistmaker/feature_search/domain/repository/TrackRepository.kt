package com.example.playlistmaker.feature_search.domain.repository

import com.example.playlistmaker.feature_search.domain.model.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): List<Track>
}