package com.example.playlistmaker.feature_search.domain.usecase

import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetSearchHistoryUseCaseImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : GetSearchHistoryUseCase {
    override fun execute(): Flow<List<Track>> {
        return searchHistoryRepository.getSearchHistory()
    }
}