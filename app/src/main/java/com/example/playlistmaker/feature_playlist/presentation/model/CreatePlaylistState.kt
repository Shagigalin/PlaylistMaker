package com.example.playlistmaker.feature_playlist.presentation.model

import android.net.Uri

data class CreatePlaylistState(
    val name: String = "",
    val description: String = "",
    val coverUri: Uri? = null,
    val coverPath: String? = null,
    val isNameValid: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)