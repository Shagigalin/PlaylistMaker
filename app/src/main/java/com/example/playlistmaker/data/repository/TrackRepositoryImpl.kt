package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.network.iTunesApi
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepositoryImpl : TrackRepository {

    private val iTunesService: iTunesApi by lazy {
        RetrofitClient.getClient().create(iTunesApi::class.java)
    }

    override suspend fun searchTracks(query: String): List<Track> {
        return withContext(Dispatchers.IO) {
            try {
                val response = iTunesService.searchTracks(query).execute()
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
}