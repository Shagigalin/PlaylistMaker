package com.example.playlistmaker.presentation.di

import android.content.Context
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.usecase.AddToSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.usecase.SearchTracksUseCase

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

    fun provideSearchTracksUseCase(): SearchTracksUseCase {
        return SearchTracksUseCase(provideTrackRepository())
    }

    fun provideGetSearchHistoryUseCase(context: Context): GetSearchHistoryUseCase {
        return GetSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideAddToSearchHistoryUseCase(context: Context): AddToSearchHistoryUseCase {
        return AddToSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideClearSearchHistoryUseCase(context: Context): ClearSearchHistoryUseCase {
        return ClearSearchHistoryUseCase(provideSearchHistoryRepository(context))
    }
}