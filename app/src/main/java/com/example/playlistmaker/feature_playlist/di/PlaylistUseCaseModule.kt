package com.example.playlistmaker.feature_playlist.di

import com.example.playlistmaker.feature_playlist.domain.repository.PlaylistRepository
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCase
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCaseImpl
import org.koin.dsl.module

val playlistUseCaseModule = module {

    factory<PlaylistUseCase> {
        PlaylistUseCaseImpl(
            repository = get()
        )
    }
}