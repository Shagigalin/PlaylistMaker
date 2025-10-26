package com.example.playlistmaker.data.di

import android.content.Context
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.usecase.AddToSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.AddToSearchHistoryUseCaseInterface
import com.example.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.ClearSearchHistoryUseCaseInterface
import com.example.playlistmaker.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.GetSearchHistoryUseCaseInterface
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.domain.usecase.SearchTracksUseCaseInterface

object Creator {

    private fun provideSharedPreferencesStorage(context: Context): SharedPreferencesStorage {
        val sharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        return SharedPreferencesStorage(sharedPreferences)
    }

    private fun provideTrackRepository(): TrackRepository {
        return TrackRepositoryImpl()
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferencesStorage(context))
    }

    fun provideSearchTracksUseCase(): SearchTracksUseCaseInterface {
        return SearchTracksUseCase(provideTrackRepository())
    }

    fun provideGetSearchHistoryUseCase(context: Context): GetSearchHistoryUseCaseInterface {
        return GetSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideAddToSearchHistoryUseCase(context: Context): AddToSearchHistoryUseCaseInterface {
        return AddToSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideClearSearchHistoryUseCase(context: Context): ClearSearchHistoryUseCaseInterface {
        return ClearSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }
}