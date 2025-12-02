package com.example.playlistmaker.feature_search.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesStorage(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    fun <T> getList(key: String, clazz: Class<T>): List<T> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = TypeToken.getParameterized(List::class.java, clazz).type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun <T> saveList(key: String, list: List<T>) {
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun clear(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}