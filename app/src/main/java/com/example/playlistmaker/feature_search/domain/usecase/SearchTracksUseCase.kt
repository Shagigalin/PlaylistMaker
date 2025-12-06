package com.example.playlistmaker.feature_search.domain.usecase

import com.example.playlistmaker.feature_search.domain.model.Track

interface SearchTracksUseCase {
    suspend fun execute(query: String): List<Track>
}