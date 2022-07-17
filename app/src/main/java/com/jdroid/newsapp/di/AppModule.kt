package com.jdroid.newsapp.di

import android.content.Context
import androidx.room.Room
import com.jdroid.newsapp.BuildConfig
import com.jdroid.newsapp.api.NewsAPI
import com.jdroid.newsapp.db.NewsDatabase
import com.jdroid.newsapp.utils.Constants
import com.jdroid.newsapp.utils.Constants.ROOM_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideBaseUrl() = Constants.BASE_URL


    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient
            .Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()


    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit) = retrofit.create(NewsAPI::class.java)

    @Provides
    @Singleton
    fun provideRoomDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(app, NewsDatabase::class.java, ROOM_DATABASE_NAME).build()


    @Provides
    @Singleton
    fun provideRunningDao(db: NewsDatabase) = db.getArticleDao()

}