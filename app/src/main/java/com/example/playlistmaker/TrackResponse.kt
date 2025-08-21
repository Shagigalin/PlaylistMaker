package com.example.playlistmaker

data class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
)

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?
) {
    fun getFormattedTime(): String {
        return if (trackTimeMillis > 0) {
            val minutes = (trackTimeMillis / 1000) / 60
            val seconds = (trackTimeMillis / 1000) % 60
            String.format("%02d:%02d", minutes, seconds)
        } else {
            "--:--"
        }
    }
}