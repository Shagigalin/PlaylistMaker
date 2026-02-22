package com.example.playlistmaker.feature_playlist.di

import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_playlist.domain.repository.PlaylistRepository
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCase
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCaseImpl
import com.example.playlistmaker.feature_playlist.presentation.create.CreatePlaylistViewModel
import com.example.playlistmaker.feature_playlist.presentation.details.PlaylistDetailsViewModel
import com.example.playlistmaker.feature_playlist.presentation.edit.EditPlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module {

    // UseCase
    factory<PlaylistUseCase> {
        PlaylistUseCaseImpl(
            repository = get()
        )
    }

    // ViewModels
    viewModel {
        CreatePlaylistViewModel(
            playlistUseCase = get()
        )
    }

    viewModel { (playlist: Playlist) ->
        EditPlaylistViewModel(
            playlistUseCase = get(),
            playlist = playlist
        )
    }

    viewModel {
        PlaylistDetailsViewModel(
            playlistUseCase = get()
        )
    }
}