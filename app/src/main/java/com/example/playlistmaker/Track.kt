package com.example.playlistmaker

import java.io.Serializable

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String? // ДОБАВЛЯЕМ previewUrl
) : Serializable {

    fun getFormattedTime(): String {
        return if (trackTimeMillis > 0) {
            val minutes = (trackTimeMillis / 1000) / 60
            val seconds = (trackTimeMillis / 1000) % 60
            String.format("%02d:%02d", minutes, seconds)
        } else {
            "--:--"
        }
    }

    // Функция для получения обложки высокого качества
    fun getCoverArtwork(): String {
        return if (artworkUrl100.isNotEmpty()) {
            artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        } else {
            ""
        }
    }

    // Функция для получения года из releaseDate
    fun getReleaseYear(): String? {
        return releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Track
        return trackId == other.trackId
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }
}