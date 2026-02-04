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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.FlowPreview::class)
class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addToSearchHistoryUseCase: AddToSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val _state = MutableLiveData<SearchState>(SearchState.Initial)
    val state: LiveData<SearchState> = _state

    private val _event = SingleLiveEvent<SearchEvent>()
    val event: LiveData<SearchEvent> = _event

    private var searchJob: Job? = null
    private var clickDebounceJob: Job? = null

    private var lastSearchQuery: String? = null
    private var lastSearchResults: List<Track> = emptyList()

    private val searchQuery = MutableStateFlow("")

    init {
        loadSearchHistory()
        setupSearchFlow()
    }

    private fun setupSearchFlow() {
        viewModelScope.launch {
            searchQuery
                .debounce(SEARCH_DEBOUNCE_DELAY)
                .distinctUntilChanged()
                .filter { query -> query.isNotEmpty() }
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun search(query: String) {

        searchQuery.value = query

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
    }

    fun performSearchDirectly(query: String) {
        lastSearchQuery = query
        performSearch(query)
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchTracksUseCase.execute(query)
                .onStart {
                    _state.value = SearchState(
                        isLoading = true,
                        isSearching = true,
                        isHistoryVisible = false,
                        isError = false,
                        isNoResults = false
                    )
                }
                .catch { e ->
                    lastSearchResults = emptyList()
                    _state.value = SearchState(
                        isLoading = false,
                        isError = true,
                        isSearching = false,
                        isHistoryVisible = false,
                        tracks = emptyList()
                    )
                    _event.value = SearchEvent(errorMessage = "Ошибка поиска: ${e.localizedMessage}")
                }
                .collect { tracks ->
                    lastSearchQuery = query
                    lastSearchResults = tracks

                    _state.value = SearchState(
                        tracks = tracks,
                        isLoading = false,
                        isSearching = tracks.isNotEmpty(),
                        isHistoryVisible = false,
                        isNoResults = tracks.isEmpty()
                    )
                }
        }
    }

    fun restorePreviousSearch() {
        lastSearchQuery?.let { query ->
            if (query.isNotEmpty()) {
                _state.value = SearchState(
                    tracks = lastSearchResults,
                    isLoading = false,
                    isSearching = lastSearchResults.isNotEmpty(),
                    isHistoryVisible = false,
                    isNoResults = lastSearchResults.isEmpty()
                )
            }
        }
    }

    fun onTrackClick(track: Track) {

        clickDebounceJob?.cancel()
        clickDebounceJob = viewModelScope.launch {
            addToHistory(track)
            _event.value = SearchEvent(navigateToPlayer = track)


            delay(CLICK_DEBOUNCE_DELAY)
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
}