package com.example.playlistmaker.feature_search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.utils.SingleLiveEvent
import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.usecase.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.playlistmaker.feature_search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.feature_search.domain.usecase.GetSearchHistoryUseCase
import com.example.playlistmaker.feature_search.domain.usecase.AddToSearchHistoryUseCase
import com.example.playlistmaker.feature_search.domain.usecase.ClearSearchHistoryUseCase

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addToSearchHistoryUseCase: AddToSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>(SearchState.Initial)
    val state: LiveData<SearchState> = _state

    private val _event = SingleLiveEvent<SearchEvent>()
    val event: LiveData<SearchEvent> = _event

    private var searchJob: Job? = null
    private val SEARCH_DEBOUNCE_DELAY = 1000L

    init {
        loadSearchHistory()
    }

    fun search(query: String) {
        searchJob?.cancel()

        if (query.isEmpty()) {
            showHistory()
            return
        }

        _state.value = SearchState(
            isLoading = true,
            isSearching = true,
            isHistoryVisible = false,
            isError = false,
            isNoResults = false
        )

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        try {
            val tracks = searchTracksUseCase.execute(query)

            _state.postValue(SearchState(
                tracks = tracks,
                isLoading = false,
                isSearching = tracks.isNotEmpty(),
                isHistoryVisible = false,
                isNoResults = tracks.isEmpty()
            ))
        } catch (e: Exception) {
            _state.postValue(SearchState(
                isLoading = false,
                isError = true,
                isSearching = false
            ))

            _event.postValue(SearchEvent(errorMessage = "Ошибка поиска: ${e.localizedMessage}"))
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
        viewModelScope.launch {
            val history = getSearchHistoryUseCase.execute()
            val hasHistory = history.isNotEmpty()

            _state.value = SearchState(
                history = history,
                isHistoryVisible = hasHistory,
                isSearching = false
            )
        }
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            val history = getSearchHistoryUseCase.execute()
            val hasHistory = history.isNotEmpty()

            // Обновляем только историю, не меняя другие состояния
            val currentState = _state.value
            if (currentState != null) {
                _state.value = currentState.copy(history = history)
            } else {
                _state.value = SearchState(
                    history = history,
                    isHistoryVisible = hasHistory
                )
            }
        }
    }

    fun navigateToPlayer(track: Track) {
        viewModelScope.launch {
            // Добавляем в историю
            addToHistory(track)

            // Создаем событие навигации
            _event.value = SearchEvent(navigateToPlayer = track)
        }
    }
}