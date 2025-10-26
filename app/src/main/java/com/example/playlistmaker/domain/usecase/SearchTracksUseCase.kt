package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackRepository

class SearchTracksUseCase(private val trackRepository: TrackRepository) {
    suspend fun execute(query: String): List<Track> {
        return trackRepository.searchTracks(query)
    }
}