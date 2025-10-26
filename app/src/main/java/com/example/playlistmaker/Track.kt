package com.example.playlistmaker

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Track(
    @SerializedName("trackId") val trackId: Long,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long,
    @SerializedName("artworkUrl100") val artworkUrl100: String,
    @SerializedName("collectionName") val collectionName: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("primaryGenreName") val primaryGenreName: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("previewUrl") val previewUrl: String?
) : Serializable {

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