package com.example.playlistmaker.domain.di

import com.example.playlistmaker.domain.usecase.FavoriteTracksUseCase
import com.example.playlistmaker.domain.usecase.FavoriteTracksUseCaseImpl
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCase
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCaseImpl
import org.koin.dsl.module

val useCaseModule = module {

    factory<FavoriteTracksUseCase> {
        FavoriteTracksUseCaseImpl(
            repository = get()
        )
    }


    factory<PlaylistUseCase> {
        PlaylistUseCaseImpl(
            repository = get()
        )
    }
}