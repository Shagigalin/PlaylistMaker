package com.example.playlistmaker.feature_search.data.repository

import com.example.playlistmaker.feature_search.data.network.iTunesApi
import com.example.playlistmaker.feature_search.data.network.RetrofitClient
import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository

class NetworkTrackRepository : TrackRepository {

    private val api: iTunesApi by lazy {
        println("DEBUG: Creating iTunesApi instance")
        RetrofitClient.getClient().create(iTunesApi::class.java)
    }

    override suspend fun searchTracks(query: String): List<Track> {
        println("NETWORK DEBUG: Searching for '$query'")

        if (query.length < 2) {
            println("NETWORK DEBUG: Query too short")
            return emptyList()
        }

        return try {
            println("NETWORK DEBUG: Making API call...")

            // Используем suspend функцию - Retrofit сам позаботится о потоках
            val response = api.searchTracks(query)
            println("NETWORK DEBUG: Response code: ${response.code()}")

            if (response.isSuccessful) {
                val tracks = response.body()?.results?.map { it.toTrack() } ?: emptyList()
                println("NETWORK DEBUG: Success! Found ${tracks.size} tracks")
                tracks.take(15)
            } else {
                println("NETWORK DEBUG: API error: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("NETWORK DEBUG: Exception: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}