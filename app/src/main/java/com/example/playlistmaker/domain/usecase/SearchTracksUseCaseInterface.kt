package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track

interface SearchTracksUseCaseInterface {
    suspend fun execute(query: String): List<Track>
}