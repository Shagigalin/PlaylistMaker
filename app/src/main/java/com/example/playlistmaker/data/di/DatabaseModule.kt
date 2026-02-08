package com.example.playlistmaker.data.di

import android.content.Context
import com.example.playlistmaker.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getInstance(androidContext()) }

    single { get<AppDatabase>().favoriteTracksDao() }
}