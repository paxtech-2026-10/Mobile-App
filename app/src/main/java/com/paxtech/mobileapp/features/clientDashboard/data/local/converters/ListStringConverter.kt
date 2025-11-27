package com.paxtech.mobileapp.features.clientDashboard.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListStringConverter {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value == null || value.isEmpty()) {
            return emptyList()
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
    
    @TypeConverter
    fun toString(list: List<String>?): String {
        if (list == null || list.isEmpty()) {
            return "[]"
        }
        return gson.toJson(list)
    }
}







