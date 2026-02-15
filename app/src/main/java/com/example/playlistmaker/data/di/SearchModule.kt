package com.example.playlistmaker.data.di

import android.app.Application
import com.example.playlistmaker.feature_search.data.network.ITunesApi
import com.example.playlistmaker.feature_search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.feature_search.data.repository.TrackRepositorySimple
import com.example.playlistmaker.feature_search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository
import com.example.playlistmaker.feature_search.domain.usecase.*
import com.example.playlistmaker.feature_search.presentation.SearchViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val searchModule = module {

    // 1. Network - независимые компоненты
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ITunesApi> {
        get<Retrofit>().create(ITunesApi::class.java)
    }

    // 2. Repositories
    factory<TrackRepository> {
        TrackRepositorySimple(
            iTunesApi = get<ITunesApi>(),
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

    // 3. Use Cases - factory
    factory<SearchTracksUseCase> {
        SearchTracksUseCaseImpl(
            trackRepository = get()
        )
    }

    factory<GetSearchHistoryUseCase> {
        GetSearchHistoryUseCaseImpl(get())
    }

    factory<AddToSearchHistoryUseCase> {
        AddToSearchHistoryUseCaseImpl(get())
    }

    factory<ClearSearchHistoryUseCase> {
        ClearSearchHistoryUseCaseImpl(get())
    }

    // 4. ViewModel
    viewModel {
        SearchViewModel(
            searchTracksUseCase = get(),
            getSearchHistoryUseCase = get(),
            addToSearchHistoryUseCase = get(),
            clearSearchHistoryUseCase = get(),
            application = androidApplication()
        )
    }
}