package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class GetSearchHistoryUseCase(
    private val searchHistoryRepository: SearchHistoryRepository
) : GetSearchHistoryUseCaseInterface {

    override fun execute(): List<Track> {
        return searchHistoryRepository.getSearchHistory()
    }
}