package com.example.playlistmaker.domain.di

import com.example.playlistmaker.domain.usecase.FavoriteTracksUseCase
import com.example.playlistmaker.domain.usecase.FavoriteTracksUseCaseImpl
import org.koin.dsl.module

val useCaseModule = module {
    factory<FavoriteTracksUseCase> {
        FavoriteTracksUseCaseImpl(
            repository = get()
        )
    }
}