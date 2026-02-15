package com.example.playlistmaker.data.di

import android.content.Context
import com.example.playlistmaker.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getInstance(androidContext()) }

    // Existing DAOs
    single { get<AppDatabase>().favoriteTracksDao() }

    // New DAOs
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().playlistTrackDao() }
}