package com.example.playlistmaker.data.di

import com.example.playlistmaker.feature_search.data.network.ITunesApi
import com.example.playlistmaker.feature_search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.feature_search.data.repository.TrackRepositorySimple
import com.example.playlistmaker.feature_search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository
import com.example.playlistmaker.feature_search.domain.usecase.*
import com.example.playlistmaker.feature_search.presentation.SearchViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.playlistmaker.feature_search.domain.usecase.SearchTracksUseCaseImpl
import com.example.playlistmaker.feature_search.domain.usecase.GetSearchHistoryUseCaseImpl
import com.example.playlistmaker.feature_search.domain.usecase.AddToSearchHistoryUseCaseImpl
import com.example.playlistmaker.feature_search.domain.usecase.ClearSearchHistoryUseCaseImpl

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
    single<TrackRepository> {
        TrackRepositorySimple(get()) // зависит от ITunesApi
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get()) // зависит от SharedPreferencesStorage
    }

    // 3. Use Cases - factory (создаются каждый раз новые)
    factory<SearchTracksUseCase> {
        SearchTracksUseCaseImpl(get()) // зависит от TrackRepository
    }

    factory<GetSearchHistoryUseCase> {
        GetSearchHistoryUseCaseImpl(get()) // зависит от SearchHistoryRepository
    }

    factory<AddToSearchHistoryUseCase> {
        AddToSearchHistoryUseCaseImpl(get()) // зависит от SearchHistoryRepository
    }

    factory<ClearSearchHistoryUseCase> {
        ClearSearchHistoryUseCaseImpl(get()) // зависит от SearchHistoryRepository
    }

    // 4. ViewModel
    viewModel {
        SearchViewModel(
            searchTracksUseCase = get(),
            getSearchHistoryUseCase = get(),
            addToSearchHistoryUseCase = get(),
            clearSearchHistoryUseCase = get()
        )
    }
}