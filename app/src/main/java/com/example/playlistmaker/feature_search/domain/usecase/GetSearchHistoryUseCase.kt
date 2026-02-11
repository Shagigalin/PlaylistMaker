package com.example.playlistmaker.feature_search.domain.usecase

import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface GetSearchHistoryUseCase {
    fun execute(): Flow<List<Track>>
}