package com.example.playlistmaker.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `playlists` (
                `playlist_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `description` TEXT,
                `cover_path` TEXT,
                `track_ids_json` TEXT NOT NULL DEFAULT '[]',
                `track_count` INTEGER NOT NULL DEFAULT 0
            )
        """)


        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `playlist_tracks` (
                `track_id` INTEGER PRIMARY KEY NOT NULL,
                `track_name` TEXT NOT NULL,
                `artist_name` TEXT NOT NULL,
                `track_time` TEXT NOT NULL,
                `artwork_url` TEXT NOT NULL,
                `collection_name` TEXT NOT NULL DEFAULT '',
                `release_date` TEXT NOT NULL DEFAULT '',
                `primary_genre_name` TEXT NOT NULL DEFAULT '',
                `country` TEXT NOT NULL DEFAULT '',
                `preview_url` TEXT NOT NULL DEFAULT '',
                `added_date` INTEGER NOT NULL
            )
        """)
    }
}