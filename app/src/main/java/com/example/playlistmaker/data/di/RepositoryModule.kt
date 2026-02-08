package com.example.playlistmaker.data.di

import com.example.playlistmaker.data.repository.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.feature_search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.feature_search.data.repository.TrackRepositorySimple
import com.example.playlistmaker.feature_search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> {
        TrackRepositorySimple(
            iTunesApi = get(),
            favoriteTracksDao = get()
        )
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            storage = get(),
            favoriteTracksDao = get(),
            gson = get()
        )
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(
            favoriteTracksDao = get()
        )
    }
}