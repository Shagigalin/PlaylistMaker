package com.example.playlistmaker.feature_playlist.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_playlist.domain.model.PlaylistDetails
import com.example.playlistmaker.feature_playlist.domain.usecase.PlaylistUseCase
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsViewModel(
    private val playlistUseCase: PlaylistUseCase
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistDetailsState>()
    val state: LiveData<PlaylistDetailsState> = _state

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> = _navigateBack

    private val _navigateToPlayer = MutableLiveData<Track?>()
    val navigateToPlayer: LiveData<Track?> = _navigateToPlayer

    private val _navigateToEdit = MutableLiveData<Boolean>()
    val navigateToEdit: LiveData<Boolean> = _navigateToEdit

    private val _showDeleteTrackDialog = MutableLiveData<Track?>()
    val showDeleteTrackDialog: LiveData<Track?> = _showDeleteTrackDialog

    private val _showDeletePlaylistDialog = MutableLiveData<Boolean>()
    val showDeletePlaylistDialog: LiveData<Boolean> = _showDeletePlaylistDialog

    private val _shareText = MutableLiveData<String?>()
    val shareText: LiveData<String?> = _shareText

    private val _showEmptyPlaylistToast = MutableLiveData<Boolean>()
    val showEmptyPlaylistToast: LiveData<Boolean> = _showEmptyPlaylistToast

    private val _isMenuVisible = MutableLiveData<Boolean>()
    val isMenuVisible: LiveData<Boolean> = _isMenuVisible

    private var currentPlaylistId: Long = 0
    private var currentDetails: PlaylistDetails? = null

    fun loadPlaylistDetails(playlistId: Long) {
        currentPlaylistId = playlistId
        _state.value = PlaylistDetailsState.Loading

        viewModelScope.launch {
            try {
                val details = playlistUseCase.getPlaylistDetails(playlistId)
                if (details != null) {
                    currentDetails = details
                    _state.value = PlaylistDetailsState.Content(details)
                } else {
                    _state.value = PlaylistDetailsState.Error("Плейлист не найден")
                }
            } catch (e: Exception) {
                _state.value = PlaylistDetailsState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun onTrackClick(track: Track) {
        _navigateToPlayer.value = track
    }

    fun onTrackLongClick(track: Track) {
        _showDeleteTrackDialog.value = track
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            try {
                val currentState = _state.value
                if (currentState is PlaylistDetailsState.Content) {
                    playlistUseCase.removeTrackFromPlaylist(currentState.details.playlist, track.trackId)
                    loadPlaylistDetails(currentPlaylistId)
                }
            } catch (e: Exception) {
                _state.value = PlaylistDetailsState.Error(e.message ?: "Ошибка удаления")
            }
        }
    }

    fun showMenu() {
        _isMenuVisible.value = true
    }

    fun hideMenu() {
        _isMenuVisible.value = false
    }

    fun onMenuShareClick() {
        hideMenu()

        val details = currentDetails
        if (details == null || details.tracks.isEmpty()) {
            _showEmptyPlaylistToast.value = true
            return
        }

        _shareText.value = buildShareText(details)
    }

    fun onMenuEditClick() {
        hideMenu()
        _navigateToEdit.value = true
    }

    fun onMenuDeleteClick() {
        hideMenu()
        _showDeletePlaylistDialog.value = true
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            try {
                currentDetails?.playlist?.let { playlist ->
                    playlistUseCase.deletePlaylist(playlist)
                    _navigateBack.value = true
                }
            } catch (e: Exception) {
                _state.value = PlaylistDetailsState.Error(e.message ?: "Ошибка удаления плейлиста")
            }
        }
    }

    private fun buildShareText(details: PlaylistDetails): String {
        val sb = StringBuilder()

        sb.append(details.playlist.name).append("\n")

        if (!details.playlist.description.isNullOrBlank()) {
            sb.append(details.playlist.description).append("\n")
        }

        val tracksWord = when (details.tracks.size % 10) {
            1 -> "трек"
            2, 3, 4 -> "трека"
            else -> "треков"
        }
        sb.append("${details.tracks.size} $tracksWord").append("\n\n")

        details.tracks.forEachIndexed { index, track ->
            val position = index + 1
            val time = formatTrackTime(track.trackTimeMillis)
            sb.append("$position. ${track.artistName} - ${track.trackName} ($time)")
            if (index < details.tracks.size - 1) {
                sb.append("\n")
            }
        }

        return sb.toString()
    }

    fun onShareHandled() {
        _shareText.value = null
    }

    fun onEmptyPlaylistToastHandled() {
        _showEmptyPlaylistToast.value = false
    }

    fun onDeleteDialogDismissed() {
        _showDeleteTrackDialog.value = null
        _showDeletePlaylistDialog.value = false
    }

    fun onNavigateToPlayerHandled() {
        _navigateToPlayer.value = null
    }

    fun onNavigateToEditHandled() {
        _navigateToEdit.value = false
    }

    fun formatDuration(millis: Long): String {
        return if (millis > 0) {
            val totalSeconds = millis / 1000
            val minutes = totalSeconds / 60
            "$minutes мин"
        } else {
            "0 мин"
        }
    }

    fun formatTrackTime(millis: Long): String {
        return if (millis > 0) {
            val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
            formatter.format(millis)
        } else {
            "00:00"
        }
    }

    fun onBackPressed() {
        _navigateBack.value = true
    }

    fun onNavigateBackHandled() {
        _navigateBack.value = false
    }
}

sealed class PlaylistDetailsState {
    object Loading : PlaylistDetailsState()
    data class Content(val details: PlaylistDetails) : PlaylistDetailsState()
    data class Error(val message: String) : PlaylistDetailsState()
}