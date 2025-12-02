package com.example.playlistmaker.feature_player.domain.usecase

class TimeFormatterUseCase {

    fun formatTime(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun formatProgress(current: Int, total: Int): Float {
        return if (total > 0) {
            current.toFloat() / total.toFloat()
        } else {
            0f
        }
    }
}