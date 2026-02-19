package com.example.playlistmaker.feature_playlist.presentation.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCase
import com.example.playlistmaker.feature_playlist.presentation.model.CreatePlaylistState
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistUseCase: PlaylistUseCase
) : ViewModel() {

    private val _state = MutableLiveData(CreatePlaylistState())
    val state: LiveData<CreatePlaylistState> = _state

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> = _navigateBack

    private val _showSuccessMessage = MutableLiveData<String?>()
    val showSuccessMessage: LiveData<String?> = _showSuccessMessage

    private val _showExitDialog = MutableLiveData<Boolean>()
    val showExitDialog: LiveData<Boolean> = _showExitDialog

    private var hasUnsavedChanges = false

    fun updateName(name: String) {
        val isValid = name.isNotBlank()
        _state.value = _state.value?.copy(
            name = name,
            isNameValid = isValid
        )
        hasUnsavedChanges = hasUnsavedChanges || name.isNotBlank()
    }

    fun updateDescription(description: String) {
        _state.value = _state.value?.copy(description = description)
        hasUnsavedChanges = hasUnsavedChanges || description.isNotBlank()
    }

    fun updateCover(uri: Uri?, path: String?) {
        _state.value = _state.value?.copy(
            coverUri = uri,
            coverPath = path
        )
        hasUnsavedChanges = hasUnsavedChanges || uri != null
    }

    fun createPlaylist() {
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

                if (playlistId > 0) {
                    _showSuccessMessage.value = currentState.name
                    _navigateBack.value = true
                    hasUnsavedChanges = false
                } else {
                    _state.value = currentState.copy(
                        error = "Ошибка при создании плейлиста",
                        isSaving = false
                    )
                }
            } catch (e: Exception) {
                _state.value = currentState.copy(
                    error = e.message ?: "Ошибка при создании плейлиста",
                    isSaving = false
                )
            }
        }
    }

    fun onBackPressed() {
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

    fun onSuccessMessageShown() {
        _showSuccessMessage.value = null
    }

    fun onNavigateBackHandled() {
        _navigateBack.value = false
    }
}