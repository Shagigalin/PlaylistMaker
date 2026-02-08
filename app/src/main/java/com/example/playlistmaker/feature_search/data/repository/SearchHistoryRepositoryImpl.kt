package com.example.playlistmaker.feature_search.data.repository

import com.example.playlistmaker.data.db.FavoriteTracksDao
import com.example.playlistmaker.feature_search.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SearchHistoryRepositoryImpl(
    private val storage: SharedPreferencesStorage,
    private val favoriteTracksDao: FavoriteTracksDao,
    private val gson: Gson
) : SearchHistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    override fun getSearchHistory(): Flow<List<Track>> = flow {
        val json = storage.getHistory()
        emit(if (json.isNotEmpty()) {
            try {
                gson.fromJson(json, Array<Track>::class.java).toList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        })
    }.map { tracks ->

        val favoriteIds = try {
            favoriteTracksDao.getAllIds().first()
        } catch (e: Exception) {
            emptyList()
        }

        tracks.map { track ->
            track.apply {
                isFavorite = trackId in favoriteIds
            }
        }
    }

    override suspend fun addToHistory(track: Track) {

        val currentHistory = try {
            getSearchHistory().first()
        } catch (e: Exception) {
            emptyList()
        }


        val newHistory = mutableListOf<Track>().apply {

            add(track.copy())


            currentHistory.forEach { existingTrack ->
                if (existingTrack.trackId != track.trackId) {
                    add(existingTrack.copy())
                }
            }


            if (size > MAX_HISTORY_SIZE) {
                removeLast()
            }
        }

        // Сохраняем историю
        saveHistory(newHistory)
    }

    override suspend fun clearHistory() {
        saveHistory(emptyList())
    }

    private fun saveHistory(tracks: List<Track>) {
        try {
            val json = gson.toJson(tracks)
            storage.saveHistory(json)
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
}