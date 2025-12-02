package com.example.playlistmaker

import android.app.Application
import android.content.Context

class PlaylistMakerApp : Application() {

    companion object {
        private var instance: PlaylistMakerApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}