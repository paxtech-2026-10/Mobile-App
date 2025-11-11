package com.paxtech.mobileapp.features.clientDashboard.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapStringConverter {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromString(value: String?): Map<String, String> {
        if (value == null || value.isEmpty()) {
            return emptyMap()
        }
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }
    
    @TypeConverter
    fun toString(map: Map<String, String>?): String {
        if (map == null || map.isEmpty()) {
            return "{}"
        }
        return gson.toJson(map)
    }
}

