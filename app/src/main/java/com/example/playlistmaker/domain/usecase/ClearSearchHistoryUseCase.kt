package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class ClearSearchHistoryUseCase(
    private val searchHistoryRepository: SearchHistoryRepository
) : ClearSearchHistoryUseCaseInterface {

    override fun execute() {
        searchHistoryRepository.clearSearchHistory()
    }
}