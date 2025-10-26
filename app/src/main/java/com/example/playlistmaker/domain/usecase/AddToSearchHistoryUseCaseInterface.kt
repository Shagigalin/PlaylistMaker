package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.model.Track

interface AddToSearchHistoryUseCaseInterface {
    fun execute(track: Track)
}