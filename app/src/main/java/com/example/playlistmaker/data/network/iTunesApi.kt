package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TrackResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    @GET("search")
    fun searchTracks(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song"
    ): Call<TrackResponseDto>

    companion object {
        const val BASE_URL = "https://itunes.apple.com/"
    }
}