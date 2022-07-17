package com.jdroid.newsapp.data.local

import androidx.room.TypeConverter
import com.jdroid.newsapp.data.remote.Source


class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name.toString()
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }

}