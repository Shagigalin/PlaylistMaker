package com.example.playlistmaker.feature_search.data.repository

import com.example.playlistmaker.feature_search.data.network.ITunesApi
import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository
import retrofit2.HttpException
import java.net.UnknownHostException

class TrackRepositorySimple(
    private val iTunesApi: ITunesApi
) : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        try {
            if (query.isNotEmpty()) {
                // Получаем Response
                val response = iTunesApi.search(query)


                if (response.isSuccessful) {

                    val responseBody = response.body()


                    if (responseBody != null && responseBody.results.isNotEmpty()) {

                        return responseBody.results.map { it.toTrack() }
                    }
                } else {

                    throw Exception("Ошибка сервера: ${response.code()}")
                }
            }
            return emptyList()
        } catch (e: HttpException) {
            throw Exception("Ошибка сервера: ${e.code()}")
        } catch (e: UnknownHostException) {
            throw Exception("Проверьте подключение к интернету")
        } catch (e: Exception) {
            throw Exception("Ошибка поиска: ${e.localizedMessage}")
        }
    }
}