package com.example.playlistmaker.feature_playlist.domain.model

import android.os.Parcelable
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistDetails(
    val playlist: Playlist,
    val tracks: List<Track>,
    val totalDuration: Long,
    val trackCount: Int
) : Parcelable