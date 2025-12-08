package com.example.playlistmaker.feature_search.data.repository

import com.example.playlistmaker.feature_search.data.network.ITunesApi
import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.net.UnknownHostException

class TrackRepositorySimple(
    private val iTunesApi: ITunesApi
) : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        return try {
            if (query.isNotEmpty()) {
                val response = iTunesApi.search(query)

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null && responseBody.results.isNotEmpty()) {
                        responseBody.results.map { it.toTrack() }
                    } else {
                        emptyList()
                    }
                } else {
                    throw Exception("Ошибка сервера: ${response.code()}")
                }
            } else {
                emptyList()
            }
        } catch (e: CancellationException) {

            throw e
        } catch (e: HttpException) {
            throw Exception("Ошибка сервера: ${e.code()}")
        } catch (e: UnknownHostException) {
            throw Exception("Проверьте подключение к интернету")
        } catch (e: Exception) {
            throw Exception("Ошибка поиска: ${e.localizedMessage}")
        }
    }
}