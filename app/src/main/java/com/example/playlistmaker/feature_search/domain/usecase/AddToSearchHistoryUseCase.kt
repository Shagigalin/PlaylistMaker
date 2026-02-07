package com.example.playlistmaker.feature_search.domain.usecase

import com.example.playlistmaker.feature_search.domain.model.Track

interface AddToSearchHistoryUseCase {
    suspend fun execute(track: Track)
}