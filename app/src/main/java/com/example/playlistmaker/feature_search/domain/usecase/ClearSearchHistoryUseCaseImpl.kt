package com.example.playlistmaker.feature_search.domain.usecase

import com.example.playlistmaker.feature_search.domain.repository.SearchHistoryRepository

class ClearSearchHistoryUseCaseImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : ClearSearchHistoryUseCase {
    override suspend fun execute() {
        searchHistoryRepository.clearHistory()
    }
}