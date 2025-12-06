package com.example.playlistmaker.feature_search.presentation

import com.example.playlistmaker.feature_search.domain.model.Track

data class SearchEvent(
    val errorMessage: String? = null,
    val navigateToPlayer: Track? = null
)