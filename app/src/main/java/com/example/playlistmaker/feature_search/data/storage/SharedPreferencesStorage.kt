package com.example.playlistmaker.feature_search.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson

class SharedPreferencesStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {

    fun saveHistory(json: String) {
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    fun getHistory(): String {
        return sharedPreferences.getString(HISTORY_KEY, "") ?: ""
    }

    companion object {
        private const val HISTORY_KEY = "search_history"
    }
}