package com.example.playlistmaker.feature_search.data.repository

import com.example.playlistmaker.feature_search.data.network.ITunesApi
import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.net.UnknownHostException

class TrackRepositorySimple(
    private val iTunesApi: ITunesApi
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
                    val tracks = responseBody.results.map { it.toTrack() }
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