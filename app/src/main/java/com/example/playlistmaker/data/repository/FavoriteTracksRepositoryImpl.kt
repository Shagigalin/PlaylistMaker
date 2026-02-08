package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.FavoriteTracksDao
import com.example.playlistmaker.data.db.FavoriteTrackEntity
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.feature_search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

class FavoriteTracksRepositoryImpl(
    private val favoriteTracksDao: FavoriteTracksDao
) : FavoriteTracksRepository {

    override suspend fun addToFavorites(track: Track) {
        val entity = FavoriteTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.getFormattedTime(),
            artworkUrl = track.artworkUrl100,
            collectionName = track.collectionName ?: "",
            releaseDate = track.releaseDate ?: "",
            primaryGenreName = track.primaryGenreName ?: "",
            country = track.country ?: "",
            previewUrl = track.previewUrl ?: "",
            addedDate = System.currentTimeMillis()
        )
        favoriteTracksDao.insert(entity)
    }

    override suspend fun removeFromFavorites(track: Track) {

        val isFavorite = favoriteTracksDao.isFavorite(track.trackId).first()
        if (isFavorite) {
            val entity = FavoriteTrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTime = track.getFormattedTime(),
                artworkUrl = track.artworkUrl100,
                collectionName = track.collectionName ?: "",
                releaseDate = track.releaseDate ?: "",
                primaryGenreName = track.primaryGenreName ?: "",
                country = track.country ?: "",
                previewUrl = track.previewUrl ?: "",
                addedDate = System.currentTimeMillis()
            )
            favoriteTracksDao.delete(entity)
        }
    }

    override fun getAllFavorites(): Flow<List<Track>> {
        return favoriteTracksDao.getAll().map { entities ->
            entities.map { entity ->
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
                    previewUrl = entity.previewUrl,
                    isFavorite = true
                )
            }
        }
    }

    override suspend fun getFavoriteIds(): List<Long> {
        return favoriteTracksDao.getAllIds().first()
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return favoriteTracksDao.isFavorite(trackId).first()
    }

    private fun parseTimeToMillis(time: String): Long {
        return try {
            val parts = time.split(":")
            if (parts.size == 2) {
                val minutes = parts[0].toLong()
                val seconds = parts[1].toLong()
                (minutes * 60 + seconds) * 1000
            } else {
                0L
            }
        } catch (e: Exception) {
            0L
        }
    }
}