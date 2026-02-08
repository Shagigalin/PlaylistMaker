package com.example.playlistmaker.feature_search.data.storage

import android.content.SharedPreferences

class SharedPreferencesStorage(
    private val sharedPreferences: SharedPreferences

) {

    fun saveHistory(json: String) {
        sharedPreferences.edit()
            .putString("search_history", json)
            .apply()
    }

    fun getHistory(): String {
        return sharedPreferences.getString("search_history", "") ?: ""
    }
}