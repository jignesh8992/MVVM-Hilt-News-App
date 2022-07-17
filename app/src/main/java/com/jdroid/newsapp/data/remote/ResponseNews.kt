package com.jdroid.newsapp.data.remote


import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Keep
data class ResponseNews(
    val articles: MutableList<Article> = ArrayList(),
    val status: String? = null,
    val totalResults: Int? = null
)


@Keep
@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String? = null
) : Serializable

@Keep
data class Source(
    val id: String? = null,
    val name: String? = null
)