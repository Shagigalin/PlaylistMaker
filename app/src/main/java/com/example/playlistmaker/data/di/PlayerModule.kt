package com.example.playlistmaker.data.di

import com.example.playlistmaker.feature_player.domain.usecase.PlayerControlsUseCase
import com.example.playlistmaker.feature_player.domain.usecase.TimeFormatterUseCase
import com.example.playlistmaker.feature_player.presentation.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    // Use Cases
    factory { PlayerControlsUseCase() }
    factory { TimeFormatterUseCase() }

    // ViewModel (с параметром track)
    viewModel { (track: com.example.playlistmaker.feature_search.domain.model.Track?) ->
        PlayerViewModel(
            track = track,
            playerControlsUseCase = get(),
            timeFormatterUseCase = get()
        )
    }
}