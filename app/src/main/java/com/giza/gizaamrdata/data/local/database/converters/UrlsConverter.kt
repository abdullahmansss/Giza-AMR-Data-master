package com.giza.gizaamrdata.data.local.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * @author hossam.
 */
class UrlsConverter {
    var gson = Gson()
    @TypeConverter
    fun toList(string: String): List<String>? {
        if (string.isEmpty()) {
            return listOf()
        }

        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(string, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return Gson().toJson(list)
    }
}
