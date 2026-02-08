package com.example.playlistmaker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class FavoriteTrackEntity(
    @PrimaryKey
    @ColumnInfo(name = "track_id")
    val trackId: Long,

    @ColumnInfo(name = "track_name")
    val trackName: String,

    @ColumnInfo(name = "artist_name")
    val artistName: String,

    @ColumnInfo(name = "track_time")
    val trackTime: String,

    @ColumnInfo(name = "artwork_url")
    val artworkUrl: String,

    @ColumnInfo(name = "collection_name")
    val collectionName: String = "",

    @ColumnInfo(name = "release_date")
    val releaseDate: String = "",

    @ColumnInfo(name = "primary_genre_name")
    val primaryGenreName: String = "",

    @ColumnInfo(name = "country")
    val country: String = "",

    @ColumnInfo(name = "preview_url")
    val previewUrl: String = "",

    @ColumnInfo(name = "added_date")
    val addedDate: Long = System.currentTimeMillis()
)