package com.example.playlistmaker.feature_search.domain.repository

import com.example.playlistmaker.feature_search.domain.model.Track

interface SearchHistoryRepository {
    fun getSearchHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
}