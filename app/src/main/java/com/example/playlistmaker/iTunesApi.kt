package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    @GET("search")
    fun searchTracks(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song"
    ): Call<TrackResponse>

    companion object {
        const val BASE_URL = "https://itunes.apple.com/"
    }
}