package com.example.playlistmaker.feature_search.presentation

import com.example.playlistmaker.feature_search.domain.model.Track

data class SearchState(
    val query: String = "",
    val tracks: List<Track> = emptyList(),
    val history: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val isHistoryVisible: Boolean = true,
    val isError: Boolean = false,
    val isNoResults: Boolean = false,
    val errorMessage: String? = null
) {
    companion object {
        val Initial = SearchState(
            isHistoryVisible = true
        )
    }
}