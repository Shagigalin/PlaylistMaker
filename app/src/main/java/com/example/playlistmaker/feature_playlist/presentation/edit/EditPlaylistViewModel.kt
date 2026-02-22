package com.example.playlistmaker.feature_playlist.presentation.edit

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCase
import com.example.playlistmaker.feature_playlist.presentation.create.CreatePlaylistViewModel
import kotlinx.coroutines.launch
import java.io.File

class EditPlaylistViewModel(
    playlistUseCase: PlaylistUseCase,
    private val playlist: Playlist
) : CreatePlaylistViewModel(playlistUseCase) {

    init {

        updateName(playlist.name)
        if (!playlist.description.isNullOrBlank()) {
            updateDescription(playlist.description)
        }
        if (!playlist.coverPath.isNullOrBlank()) {
            val file = File(playlist.coverPath)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                updateCover(uri, playlist.coverPath)
            }
        }
    }

    override fun createPlaylist() {
        val currentState = _state.value ?: return
        if (!currentState.isNameValid) return

        viewModelScope.launch {
            _state.value = currentState.copy(isSaving = true)

            try {
                val updatedPlaylist = Playlist(
                    id = playlist.id,
                    name = currentState.name,
                    description = currentState.description.takeIf { it.isNotBlank() },
                    coverPath = currentState.coverPath ?: playlist.coverPath,
                    trackIds = playlist.trackIds,
                    trackCount = playlist.trackCount
                )


                playlistUseCase.updatePlaylist(updatedPlaylist)

                _showSuccessMessage.value = currentState.name
                _navigateBack.value = true
                hasUnsavedChanges = false
            } catch (e: Exception) {
                _state.value = currentState.copy(
                    error = e.message ?: "Ошибка при обновлении плейлиста",
                    isSaving = false
                )
            }
        }
    }

    override fun onBackPressed() {

        _navigateBack.value = true
    }
}