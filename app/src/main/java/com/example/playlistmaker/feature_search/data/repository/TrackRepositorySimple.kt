package com.example.playlistmaker.feature_search.data.repository

import com.example.playlistmaker.data.db.FavoriteTracksDao
import com.example.playlistmaker.feature_search.data.network.ITunesApi
import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.net.UnknownHostException

class TrackRepositorySimple(
    private val iTunesApi: ITunesApi,
    private val favoriteTracksDao: FavoriteTracksDao
) : TrackRepository {

    override fun searchTracks(query: String): Flow<List<Track>> = flow {
        try {
            if (query.isEmpty()) {
                emit(emptyList())
                return@flow
            }

            val response = iTunesApi.search(query)

            if (response.isSuccessful) {
                val responseBody = response.body()

                if (responseBody != null && responseBody.results.isNotEmpty()) {
                    // Получаем список ID избранных треков
                    val favoriteIdsFlow = favoriteTracksDao.getAllIds()
                    val favoriteIds = try {

                        favoriteIdsFlow.firstOrNull() ?: emptyList()
                    } catch (e: Exception) {
                        emptyList()
                    }

                    val tracks = responseBody.results.map { dto ->
                        val track = dto.toTrack()
                        track.isFavorite = track.trackId in favoriteIds
                        track
                    }

                    emit(tracks)
                } else {
                    emit(emptyList())
                }
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            throw Exception("Ошибка сервера: ${e.code()}")
        } catch (e: UnknownHostException) {
            throw Exception("Проверьте подключение к интернету")
        } catch (e: Exception) {
            throw Exception("Ошибка поиска: ${e.localizedMessage}")
        }
    }
}