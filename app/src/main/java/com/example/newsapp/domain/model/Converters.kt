package com.example.newsapp.domain.model

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// this class is for source class to be converted for room
class Converters {

    private val gson = Gson()


    @TypeConverter
    fun fromSource(source: Source): String {
        return gson.toJson(source)
    }

    @TypeConverter
    fun toSource(sourceString: String): Source? {
        val type = object : TypeToken<Source>() {}.type
        return gson.fromJson(sourceString, type)
    }
}