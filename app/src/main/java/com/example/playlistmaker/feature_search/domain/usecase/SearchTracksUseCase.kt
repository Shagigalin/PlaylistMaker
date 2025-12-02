package com.example.playlistmaker.feature_search.domain.usecase

import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository

interface SearchTracksUseCase {
    suspend fun execute(query: String): List<Track>
}

// РЕАЛИЗАЦИЯ с суффиксом Impl
class SearchTracksUseCaseImpl(
    private val trackRepository: TrackRepository
) : SearchTracksUseCase {

    override suspend fun execute(query: String): List<Track> {
        println("PLAYLISTMAKER_DEBUG: SearchTracksUseCase executing query: $query")
        return trackRepository.searchTracks(query)
    }
}