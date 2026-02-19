package com.example.playlistmaker.feature_playlist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackIds: List<Long> = emptyList(),
    val trackCount: Int = 0
) : Parcelable