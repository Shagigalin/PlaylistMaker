package com.example.playlistmaker.feature_playlist.domain.usecase

import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_playlist.domain.model.PlaylistDetails
import com.example.playlistmaker.feature_playlist.domain.repository.PlaylistRepository
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistUseCase {
    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylistById(playlistId: Long): Playlist?

    suspend fun getPlaylistDetails(playlistId: Long): PlaylistDetails?

    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Result<Unit>

    suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Long)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun isTrackInPlaylist(playlist: Playlist, trackId: Long): Boolean
}

class PlaylistUseCaseImpl(
    private val repository: PlaylistRepository
) : PlaylistUseCase {

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long = repository.createPlaylist(name, description, coverPath)

    override fun getAllPlaylists(): Flow<List<Playlist>> = repository.getAllPlaylists()

    override suspend fun getPlaylistById(playlistId: Long): Playlist? =
        repository.getPlaylistById(playlistId)

    override suspend fun getPlaylistDetails(playlistId: Long): PlaylistDetails? {
        val playlist = repository.getPlaylistById(playlistId) ?: return null
        val tracks = repository.getTracksForPlaylist(playlist.trackIds)
        val totalDuration = tracks.sumOf { it.trackTimeMillis }

        return PlaylistDetails(
            playlist = playlist,
            tracks = tracks,
            totalDuration = totalDuration,
            trackCount = tracks.size
        )
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Result<Unit> =
        repository.addTrackToPlaylist(playlist, track)

    override suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Long) {
        repository.removeTrackFromPlaylist(playlist, trackId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun isTrackInPlaylist(playlist: Playlist, trackId: Long): Boolean =
        repository.isTrackInPlaylist(playlist, trackId)
}