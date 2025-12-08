package com.example.playlistmaker.data.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.feature_search.data.storage.SharedPreferencesStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.google.gson.Gson

val appModule = module {

    single { Gson() }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    // SharedPreferencesStorage для истории поиска
    single<SharedPreferencesStorage> {
        val sharedPreferences = androidContext()
            .getSharedPreferences("search_history", Context.MODE_PRIVATE)
        SharedPreferencesStorage(
            sharedPreferences = sharedPreferences,
            gson = get() )
    }
}