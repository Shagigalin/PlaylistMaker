package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistTrackDao
import com.example.playlistmaker.data.db.PlaylistTrackEntity
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
                    trackIds = gson.fromJson(entity.trackIdsJson, Array<Long>::class.java).toList(),
                    trackCount = entity.trackCount
                )
            }
        }
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistDao.getPlaylistById(playlistId)?.let { entity ->
            Playlist(
                id = entity.playlistId,
                name = entity.name,
                description = entity.description,
                coverPath = entity.coverPath,
                trackIds = gson.fromJson(entity.trackIdsJson, Array<Long>::class.java).toList(),
                trackCount = entity.trackCount
            )
        }
    }

    override suspend fun getTracksForPlaylist(trackIds: List<Long>): List<Track> {
        if (trackIds.isEmpty()) return emptyList()

        val trackEntities = playlistTrackDao.getTracksByIds(trackIds)
        return trackEntities.map { entity ->
            Track(
                trackId = entity.trackId,
                trackName = entity.trackName,
                artistName = entity.artistName,
                trackTimeMillis = parseTimeToMillis(entity.trackTime),
                artworkUrl100 = entity.artworkUrl,
                collectionName = entity.collectionName,
                releaseDate = entity.releaseDate,
                primaryGenreName = entity.primaryGenreName,
                country = entity.country,
                previewUrl = entity.previewUrl
            )
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

    override suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Long) {
        val updatedTrackIds = playlist.trackIds.toMutableList().apply {
            remove(trackId)
        }
        val updatedTrackIdsJson = gson.toJson(updatedTrackIds)

        val updatedPlaylistEntity = PlaylistEntity(
            playlistId = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIdsJson = updatedTrackIdsJson,
            trackCount = playlist.trackCount - 1
        )
        playlistDao.updatePlaylist(updatedPlaylistEntity)

        val allPlaylists = playlistDao.getAllPlaylistsSync()
        val isTrackInOtherPlaylist = allPlaylists.any { playlistEntity ->
            playlistEntity.playlistId != playlist.id &&
                    gson.fromJson(playlistEntity.trackIdsJson, Array<Long>::class.java).toList().contains(trackId)
        }

        if (!isTrackInOtherPlaylist) {
            playlistTrackDao.deleteTrack(trackId)
        }
    }

    override suspend fun isTrackInPlaylist(playlist: Playlist, trackId: Long): Boolean {
        return playlist.trackIds.contains(trackId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = PlaylistEntity(
            playlistId = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIdsJson = gson.toJson(playlist.trackIds),
            trackCount = playlist.trackCount
        )
        playlistDao.updatePlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        // 1. Удаляем плейлист
        playlistDao.deletePlaylist(playlist.id)

        // 2. Проверяем каждый трек из плейлиста
        val allPlaylists = playlistDao.getAllPlaylistsSync()

        playlist.trackIds.forEach { trackId ->
            val isTrackInOtherPlaylist = allPlaylists.any { playlistEntity ->
                playlistEntity.playlistId != playlist.id &&
                        gson.fromJson(playlistEntity.trackIdsJson, Array<Long>::class.java).toList().contains(trackId)
            }

            // 3. Если трек больше нигде не используется, удаляем его
            if (!isTrackInOtherPlaylist) {
                playlistTrackDao.deleteTrack(trackId)
            }
        }
    }

    private fun parseTimeToMillis(time: String): Long {
        return try {
            val parts = time.split(":")
            if (parts.size == 2) {
                parts[0].toLong() * 60 * 1000 + parts[1].toLong() * 1000
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }
}