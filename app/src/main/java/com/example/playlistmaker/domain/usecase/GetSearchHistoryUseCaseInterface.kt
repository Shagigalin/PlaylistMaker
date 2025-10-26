package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track

interface GetSearchHistoryUseCaseInterface {
    fun execute(): List<Track>
}