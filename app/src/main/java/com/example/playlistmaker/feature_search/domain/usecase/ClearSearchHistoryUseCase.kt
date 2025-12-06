package com.example.playlistmaker.feature_search.domain.usecase

import com.example.playlistmaker.feature_search.domain.repository.SearchHistoryRepository

interface ClearSearchHistoryUseCase {
    fun execute()
}

// РЕАЛИЗАЦИЯ
class ClearSearchHistoryUseCaseImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : ClearSearchHistoryUseCase {

    override fun execute() {
        searchHistoryRepository.clearSearchHistory()
    }
}