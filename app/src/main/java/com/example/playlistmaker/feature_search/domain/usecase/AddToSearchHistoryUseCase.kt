package com.example.playlistmaker.feature_search.domain.usecase

import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.SearchHistoryRepository

interface AddToSearchHistoryUseCase {
    fun execute(track: Track)
}

// РЕАЛИЗАЦИЯ
class AddToSearchHistoryUseCaseImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : AddToSearchHistoryUseCase {

    override fun execute(track: Track) {
        searchHistoryRepository.addTrackToHistory(track)
    }
}