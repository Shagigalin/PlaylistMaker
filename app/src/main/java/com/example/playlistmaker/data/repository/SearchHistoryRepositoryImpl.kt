package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val sharedPreferencesStorage: SharedPreferencesStorage
) : SearchHistoryRepository {


    override fun getSearchHistory(): List<Track> {
        return sharedPreferencesStorage.getList(SEARCH_HISTORY_KEY, Track::class.java)
    }

    override fun addTrackToHistory(track: Track) {
        val currentHistory = getSearchHistory().toMutableList()

        // Удаляем дубликаты
        currentHistory.removeAll { it.trackId == track.trackId }

        // Добавляем в начало
        currentHistory.add(0, track)

        // Ограничиваем размер истории
        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }

        sharedPreferencesStorage.saveList(SEARCH_HISTORY_KEY, currentHistory)
    }

    override fun clearSearchHistory() {
        sharedPreferencesStorage.clear(SEARCH_HISTORY_KEY)
    }

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }
}