package com.example.playlistmaker.feature_search.data.repository

import com.example.playlistmaker.data.db.FavoriteTracksDao
import com.example.playlistmaker.feature_search.data.storage.SharedPreferencesStorage
import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first

class SearchHistoryRepositoryImpl(
    private val storage: SharedPreferencesStorage,
    private val favoriteTracksDao: FavoriteTracksDao,
    private val gson: Gson
) : SearchHistoryRepository {

    companion object {
        private const val HISTORY_KEY = "search_history"
    }

    override fun getSearchHistory(): Flow<List<Track>> = flow {
        val json = storage.getHistory()
        if (json.isNotEmpty()) {
            try {
                val tracks = gson.fromJson(json, Array<Track>::class.java).toList()


                val favoriteIds = try {
                    favoriteTracksDao.getAllIds().first()
                } catch (e: Exception) {
                    emptyList()
                }


                val updatedTracks = tracks.map { track ->
                    track.apply {
                        isFavorite = favoriteIds.contains(trackId)
                    }
                }

                emit(updatedTracks)
            } catch (e: Exception) {
                emit(emptyList())
            }
        } else {
            emit(emptyList())
        }
    }

    override suspend fun addToHistory(track: Track) {
        val currentHistory = getCurrentHistory()
        val newHistory = currentHistory
            .filter { it.trackId != track.trackId }
            .toMutableList()

        newHistory.add(0, track)

        // Ограничиваем размер истории
        if (newHistory.size > 10) {
            newHistory.removeLast()
        }

        saveHistory(newHistory)
    }

    override suspend fun clearHistory() {
        saveHistory(emptyList())
    }

    private suspend fun getCurrentHistory(): List<Track> {
        return try {
            getSearchHistory().first()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        storage.saveHistory(json)
    }
}