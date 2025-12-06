package com.example.playlistmaker.feature_search.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.feature_search.data.repository.NetworkTrackRepository
import com.example.playlistmaker.feature_search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.feature_search.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.feature_search.domain.usecase.*
import com.example.playlistmaker.feature_search.presentation.SearchViewModel

object SearchComponent {

    fun createViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                println("DEBUG: Creating ViewModel factory with NETWORK repository")

                // Используем NetworkTrackRepository вместо TrackRepositorySimple
                val trackRepository = NetworkTrackRepository()

                val sharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
                val storage = SharedPreferencesStorage(sharedPreferences)
                val searchHistoryRepository = SearchHistoryRepositoryImpl(storage)

                val searchTracksUseCase = SearchTracksUseCaseImpl(trackRepository)
                val getSearchHistoryUseCase = GetSearchHistoryUseCaseImpl(searchHistoryRepository)
                val addToSearchHistoryUseCase = AddToSearchHistoryUseCaseImpl(searchHistoryRepository)
                val clearSearchHistoryUseCase = ClearSearchHistoryUseCaseImpl(searchHistoryRepository)

                return SearchViewModel(
                    searchTracksUseCase = searchTracksUseCase,
                    getSearchHistoryUseCase = getSearchHistoryUseCase,
                    addToSearchHistoryUseCase = addToSearchHistoryUseCase,
                    clearSearchHistoryUseCase = clearSearchHistoryUseCase
                ) as T
            }
        }
    }
}