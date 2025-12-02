package com.example.playlistmaker.feature_search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.usecase.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addToSearchHistoryUseCase: AddToSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>(SearchState.Initial)
    val state: LiveData<SearchState> = _state

    private var searchJob: Job? = null
    private val SEARCH_DEBOUNCE_DELAY = 1000L

    init {
        println("DEBUG: SearchViewModel created")
        loadSearchHistory()
    }

    fun search(query: String) {
        println("DEBUG: SearchViewModel.search called with: '$query'")
        searchJob?.cancel()

        if (query.isEmpty()) {
            showHistory()
            return
        }

        _state.value = SearchState(
            query = query,
            isLoading = true,
            isSearching = true,
            isHistoryVisible = false,
            isError = false,
            isNoResults = false,
            errorMessage = null
        )

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        println("DEBUG: performSearch called with: '$query'")
        try {
            val tracks = searchTracksUseCase.execute(query)
            println("DEBUG: Received ${tracks.size} tracks")

            _state.postValue(_state.value?.copy(
                tracks = tracks,
                isLoading = false,
                isSearching = tracks.isNotEmpty(),
                isNoResults = tracks.isEmpty()
            ))
        } catch (e: Exception) {
            println("DEBUG: Search error: ${e.message}")
            e.printStackTrace()
            _state.postValue(_state.value?.copy(
                isLoading = false,
                isError = true,
                isSearching = false,
                errorMessage = "Ошибка: ${e.localizedMessage}"
            ))
        }
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch {
            addToSearchHistoryUseCase.execute(track)
            loadSearchHistory()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearSearchHistoryUseCase.execute()
            loadSearchHistory()
        }
    }

    fun showHistory() {
        loadSearchHistory()
        _state.value = SearchState(
            isHistoryVisible = true,
            history = _state.value?.history ?: emptyList(),
            isSearching = false
        )
    }

    private fun loadSearchHistory() {
        val history = getSearchHistoryUseCase.execute()
        _state.value = _state.value?.copy(
            history = history,
            isHistoryVisible = history.isNotEmpty()
        ) ?: SearchState(history = history, isHistoryVisible = history.isNotEmpty())
    }
}