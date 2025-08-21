package com.example.playlistmaker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(iTunesApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: iTunesApi by lazy {
        retrofit.create(iTunesApi::class.java)
    }
}