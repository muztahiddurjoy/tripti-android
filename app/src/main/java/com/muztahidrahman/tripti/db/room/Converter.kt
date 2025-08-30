package com.muztahidrahman.tripti.db.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // For List<String>
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String): List<String> {
        if (json.isEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}