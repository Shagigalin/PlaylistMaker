package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [
        FavoriteTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "playlist_maker.db"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}