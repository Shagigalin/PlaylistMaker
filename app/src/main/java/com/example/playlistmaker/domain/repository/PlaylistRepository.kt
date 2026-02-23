package com.example.playlistmaker.feature_playlist.domain.repository

import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun getPlaylistById(playlistId: Long): Playlist?

    suspend fun getTracksForPlaylist(trackIds: List<Long>): List<Track>

    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Result<Unit>

    suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Long)

    suspend fun isTrackInPlaylist(playlist: Playlist, trackId: Long): Boolean

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)
}