package com.example.playlistmaker.feature_search.data.repository

import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.domain.repository.TrackRepository

class TrackRepositorySimple : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        println("DEBUG: Simple repository searching for: '$query'")

        // Временно возвращаем тестовые данные
        return if (query.isNotEmpty()) {
            listOf(
                Track(
                    trackId = 1,
                    trackName = "Network Test: $query",
                    artistName = "Real Artist",
                    trackTimeMillis = 180000,
                    artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/ae/4c/d4/ae4cd42a-80a9-28c6-f5f6-3d66b7a52578/886447913320.jpg/100x100bb.jpg",
                    collectionName = "Real Album",
                    releaseDate = "2023",
                    primaryGenreName = "Pop",
                    country = "US",
                    previewUrl = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview115/v4/5e/5a/38/5e5a38df-8a05-8e65-4a11-1e118e7864d5/mzaf_13341178152201361485.plus.aac.p.m4a"
                )
            )
        } else {
            emptyList()
        }
    }
}