package com.example.playlistmaker.feature_search.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
    val previewUrl: String?
) : Parcelable {

    fun getFormattedTime(): String {
        return if (trackTimeMillis > 0) {
            val totalSeconds = trackTimeMillis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            String.format("%02d:%02d", minutes, seconds)
        } else {
            "--:--"
        }
    }

    fun getCoverArtwork(): String {
        return if (artworkUrl100.isNotEmpty()) {
            artworkUrl100.replace("100x100", "512x512")
        } else {
            ""
        }
    }

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