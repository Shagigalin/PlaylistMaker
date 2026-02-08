package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: FavoriteTrackEntity)

    @Delete
    suspend fun delete(track: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_tracks ORDER BY added_date DESC")
    fun getAll(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT track_id FROM favorite_tracks")
    fun getAllIds(): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE track_id = :trackId)")
    fun isFavorite(trackId: Long): Flow<Boolean>
}