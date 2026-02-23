package com.example.playlistmaker.feature_playlist.presentation.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCase
import com.example.playlistmaker.feature_playlist.presentation.model.CreatePlaylistState
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    // Изменяем с private на protected
    protected val playlistUseCase: PlaylistUseCase
) : ViewModel() {

    protected val _state = MutableLiveData(CreatePlaylistState())
    val state: LiveData<CreatePlaylistState> = _state

    protected val _navigateBack = MutableLiveData(false)
    val navigateBack: LiveData<Boolean> = _navigateBack

    protected val _showSuccessMessage = MutableLiveData<String?>(null)
    val showSuccessMessage: LiveData<String?> = _showSuccessMessage

    protected val _showExitDialog = MutableLiveData(false)
    val showExitDialog: LiveData<Boolean> = _showExitDialog

    protected var hasUnsavedChanges = false

    open fun updateName(name: String) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(
            name = name,
            isNameValid = name.isNotBlank()
        )
        hasUnsavedChanges = true
    }

    open fun updateDescription(description: String) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(description = description)
        hasUnsavedChanges = true
    }

    open fun updateCover(uri: Uri, path: String) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(coverUri = uri, coverPath = path)
        hasUnsavedChanges = true
    }

    open fun createPlaylist() {
        val currentState = _state.value ?: return
        if (!currentState.isNameValid) return

        viewModelScope.launch {
            _state.value = currentState.copy(isSaving = true)

            try {
                val playlistId = playlistUseCase.createPlaylist(
                    name = currentState.name,
                    description = currentState.description.takeIf { it.isNotBlank() },
                    coverPath = currentState.coverPath
                )

                _showSuccessMessage.value = currentState.name
                _navigateBack.value = true
                hasUnsavedChanges = false
            } catch (e: Exception) {
                _state.value = currentState.copy(
                    error = e.message ?: "Ошибка при создании плейлиста",
                    isSaving = false
                )
            }
        }
    }

    open fun onBackPressed() {
        if (hasUnsavedChanges) {
            _showExitDialog.value = true
        } else {
            _navigateBack.value = true
        }
    }

    fun onExitConfirmed() {
        _navigateBack.value = true
        hasUnsavedChanges = false
    }

    fun onExitDialogDismissed() {
        _showExitDialog.value = false
    }

    fun onNavigateBackHandled() {
        _navigateBack.value = false
    }

    fun onSuccessMessageShown() {
        _showSuccessMessage.value = null
    }
}