package com.example.playlistmaker.feature_player.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.feature_player.domain.usecase.PlayerControlsUseCase
import com.example.playlistmaker.feature_player.domain.usecase.TimeFormatterUseCase
import com.example.playlistmaker.feature_player.presentation.PlayerViewModel

object PlayerComponent {

    private fun providePlayerControlsUseCase(): PlayerControlsUseCase {
        return PlayerControlsUseCase()
    }

    private fun provideTimeFormatterUseCase(): TimeFormatterUseCase {
        return TimeFormatterUseCase()
    }

    val viewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val playerControlsUseCase = providePlayerControlsUseCase()
            val timeFormatterUseCase = provideTimeFormatterUseCase()

            return PlayerViewModel(
                playerControlsUseCase = playerControlsUseCase,
                timeFormatterUseCase = timeFormatterUseCase
            ) as T
        }
    }
}