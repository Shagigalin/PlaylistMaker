package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class ClearSearchHistoryUseCase(private val searchHistoryRepository: SearchHistoryRepository) {
    fun execute() {
        searchHistoryRepository.clearSearchHistory()
    }
}