package com.example.playlistmaker.feature_player.data

import android.media.MediaPlayer

class MediaPlayerProvider {
    fun createMediaPlayer(): MediaPlayer {
        return MediaPlayer().apply {
            setOnPreparedListener(null)
            setOnCompletionListener(null)
            setOnErrorListener(null)
        }
    }
}