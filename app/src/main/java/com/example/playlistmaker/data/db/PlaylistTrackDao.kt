package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Delete
    suspend fun deleteTrack(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE track_id = :trackId")
    suspend fun deleteTrack(trackId: Long)

    @Query("SELECT * FROM playlist_tracks WHERE track_id IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Long>): List<PlaylistTrackEntity>

    @Query("SELECT * FROM playlist_tracks WHERE track_id = :trackId")
    suspend fun getTrackById(trackId: Long): PlaylistTrackEntity?

}