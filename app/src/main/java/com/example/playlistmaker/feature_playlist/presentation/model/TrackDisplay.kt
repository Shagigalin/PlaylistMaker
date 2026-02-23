package com.example.playlistmaker.feature_playlist.presentation.model

import com.example.playlistmaker.feature_search.domain.model.Track

data class TrackDisplay(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl: String
) {
    constructor(track: Track) : this(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        trackTime = track.getFormattedTime(),
        artworkUrl = prepareArtworkUrl(track.artworkUrl100)
    )

    companion object {
        private fun prepareArtworkUrl(originalUrl: String): String {
            return originalUrl.replace("100x100", "45x45")
        }
    }
}