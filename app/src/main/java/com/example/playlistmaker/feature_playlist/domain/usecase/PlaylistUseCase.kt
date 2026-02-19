package com.example.playlistmaker.feature_playlist.domain.usecase

import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_playlist.domain.repository.PlaylistRepository
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistUseCase {
    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long

    suspend fun getPlaylistById(id: Long): Playlist?

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Result<Unit>

    suspend fun isTrackInPlaylist(playlist: Playlist, trackId: Long): Boolean
}

class PlaylistUseCaseImpl(
    private val repository: PlaylistRepository
) : PlaylistUseCase {

    override suspend fun getPlaylistById(id: Long): Playlist? =
        repository.getPlaylistById(id)

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long = repository.createPlaylist(name, description, coverPath)

    override fun getAllPlaylists(): Flow<List<Playlist>> = repository.getAllPlaylists()

    override suspend fun addTrackToPlaylist(
        playlist: Playlist,
        track: Track
    ): Result<Unit> = repository.addTrackToPlaylist(playlist, track)

    override suspend fun isTrackInPlaylist(
        playlist: Playlist,
        trackId: Long
    ): Boolean = repository.isTrackInPlaylist(playlist, trackId)
}