package com.jdroid.newsapp.data.repo

import com.jdroid.newsapp.data.remote.Article
import com.jdroid.newsapp.data.remote.NewsAPI
import com.jdroid.newsapp.data.local.NewsDatabase
import javax.inject.Inject

class NewsRepository @Inject constructor(private val db: NewsDatabase, private val newsAPI: NewsAPI) {

    suspend fun getBreakingNews(countryCode: String, page: Int) = newsAPI.getHeadlines(countryCode, page)

    suspend fun searchNews(searchQuery: String, page: Int) = newsAPI.getSearchNews(searchQuery, page)

    suspend fun upsertNews(article: Article) = db.getArticleDao().upsert(article)

    suspend fun deleteNews(article: Article) = db.getArticleDao().delete(article)

    fun getSavedNews() = db.getArticleDao().getArticles()

}