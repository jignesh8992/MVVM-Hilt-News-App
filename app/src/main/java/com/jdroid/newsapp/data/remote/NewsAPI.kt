package com.jdroid.newsapp.data.remote

import com.jdroid.newsapp.utilities.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("top-headlines")
    suspend fun getHeadlines(
        @Query("country") country: String = "in",
        @Query("page") page: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<ResponseNews>

    @GET("top-headlines")
    suspend fun getSearchNews(
        @Query("q") country: String,
        @Query("page") page: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<ResponseNews>
}