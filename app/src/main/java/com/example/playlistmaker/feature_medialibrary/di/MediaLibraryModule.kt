package com.example.playlistmaker.feature_medialibrary.di

import com.example.playlistmaker.feature_medialibrary.presentation.favorites.FavoritesViewModel
import com.example.playlistmaker.feature_medialibrary.presentation.playlist.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {

    // ViewModels
    viewModel { PlaylistViewModel() }
    viewModel { FavoritesViewModel() }
}