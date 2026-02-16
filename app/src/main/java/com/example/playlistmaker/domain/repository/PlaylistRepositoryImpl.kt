package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistTrackDao
import com.example.playlistmaker.data.db.PlaylistTrackEntity
import com.example.playlistmaker.feature_player.presentation.AddToPlaylistResult
import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_playlist.domain.repository.PlaylistRepository
import com.example.playlistmaker.feature_search.domain.model.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?
    ): Long {
        val playlistEntity = PlaylistEntity(
            name = name,
            description = description,
            coverPath = coverPath,
            trackIdsJson = "[]",
            trackCount = 0
        )
        return playlistDao.insertPlaylist(playlistEntity)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { entity ->
                Playlist(
                    id = entity.playlistId,
                    name = entity.name,
                    description = entity.description,
                    coverPath = entity.coverPath,
                    trackIds = try {
                        gson.fromJson(entity.trackIdsJson, Array<Long>::class.java).toList()
                    } catch (e: Exception) {
                        emptyList()
                    },
                    trackCount = entity.trackCount
                )
            }
        }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Result<Unit> {
        return try {

            val trackEntity = PlaylistTrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTime = track.getFormattedTime(),
                artworkUrl = track.artworkUrl100,
                collectionName = track.collectionName ?: "",
                releaseDate = track.releaseDate ?: "",
                primaryGenreName = track.primaryGenreName ?: "",
                country = track.country ?: "",
                previewUrl = track.previewUrl ?: ""
            )
            playlistTrackDao.insertTrack(trackEntity)


            val updatedTrackIds = playlist.trackIds.toMutableList().apply {
                add(track.trackId)
            }
            val updatedTrackIdsJson = gson.toJson(updatedTrackIds)

            val updatedPlaylistEntity = PlaylistEntity(
                playlistId = playlist.id,
                name = playlist.name,
                description = playlist.description,
                coverPath = playlist.coverPath,
                trackIdsJson = updatedTrackIdsJson,
                trackCount = playlist.trackCount + 1
            )
            playlistDao.updatePlaylist(updatedPlaylistEntity)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isTrackInPlaylist(playlist: Playlist, trackId: Long): Boolean {
        return playlist.trackIds.contains(trackId)
    }
}