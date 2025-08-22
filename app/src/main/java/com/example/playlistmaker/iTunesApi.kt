package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    @GET("/search?entity=song")
    fun searchTracks(
        @Query("term") searchTerm: String,
        @Query("limit") limit: Int = 50
    ): Call<TrackResponse>  // Изменили на Call<>

    companion object {
        const val BASE_URL = "https://itunes.apple.com"
    }
}