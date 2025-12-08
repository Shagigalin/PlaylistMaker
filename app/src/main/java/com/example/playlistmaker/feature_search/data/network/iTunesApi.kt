package com.example.playlistmaker.feature_search.data.network

import com.example.playlistmaker.feature_search.data.dto.TrackResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 50
    ): Response<TrackResponseDto>
}