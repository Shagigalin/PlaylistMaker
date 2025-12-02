package com.example.playlistmaker.feature_search.domain.usecase

import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.SearchHistoryRepository

interface GetSearchHistoryUseCase {
    fun execute(): List<Track>
}

// РЕАЛИЗАЦИЯ
class GetSearchHistoryUseCaseImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : GetSearchHistoryUseCase {

    override fun execute(): List<Track> {
        return searchHistoryRepository.getSearchHistory()
    }
}