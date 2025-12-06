package com.example.playlistmaker.feature_search.domain.usecase

import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository


class SearchTracksUseCaseImpl(
    private val trackRepository: TrackRepository
) : SearchTracksUseCase {
    override suspend fun execute(query: String): List<Track> {
        return trackRepository.searchTracks(query)
    }
}