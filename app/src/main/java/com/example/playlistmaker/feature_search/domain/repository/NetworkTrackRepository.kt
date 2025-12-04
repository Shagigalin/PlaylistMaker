package com.example.playlistmaker.feature_search.data.repository

import com.example.playlistmaker.feature_search.data.network.iTunesApi
import com.example.playlistmaker.feature_search.data.network.RetrofitClient
import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository

class NetworkTrackRepository : TrackRepository {

    private val api: iTunesApi by lazy {
        RetrofitClient.getClient().create(iTunesApi::class.java)
    }

    override suspend fun searchTracks(query: String): List<Track> {
        if (query.length < 2) return emptyList()

        return try {
            val response = api.searchTracks(query)

            if (response.isSuccessful) {
                response.body()?.results?.map { it.toTrack() } ?: emptyList()

            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}