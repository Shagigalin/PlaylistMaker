package com.example.playlistmaker.feature_search.domain.usecase

import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow

class SearchTracksUseCaseImpl(
    private val trackRepository: TrackRepository
) : SearchTracksUseCase {
    override fun execute(query: String): Flow<List<Track>> {
        return trackRepository.searchTracks(query)
    }
}