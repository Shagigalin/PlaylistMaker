package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class AddToSearchHistoryUseCase(
    private val searchHistoryRepository: SearchHistoryRepository
) : AddToSearchHistoryUseCaseInterface {

    override fun execute(track: Track) {
        searchHistoryRepository.addTrackToHistory(track)
    }
}