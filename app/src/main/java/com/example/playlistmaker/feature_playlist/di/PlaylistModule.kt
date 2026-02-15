package com.example.playlistmaker.feature_playlist.di

import com.example.playlistmaker.feature_playlist.domain.repository.PlaylistRepository
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCase
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCaseImpl
import com.example.playlistmaker.feature_playlist.presentation.create.CreatePlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module {

    // UseCase
    factory<PlaylistUseCase> {
        PlaylistUseCaseImpl(
            repository = get()
        )
    }

    // ViewModel
    viewModel {
        CreatePlaylistViewModel(
            playlistUseCase = get()
        )
    }
}