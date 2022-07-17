package com.jdroid.newsapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jdroid.newsapp.api.Article
import com.jdroid.newsapp.api.Converters

@Database(entities = [Article::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

}