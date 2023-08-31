package com.tommyvisic.spaceflightnews.core.di

import android.content.Context
import androidx.room.Room
import com.tommyvisic.spaceflightnews.core.data.ArticlesRepository
import com.tommyvisic.spaceflightnews.core.data.DefaultArticlesRepository
import com.tommyvisic.spaceflightnews.core.data.local.ArticlesDao
import com.tommyvisic.spaceflightnews.core.data.local.ArticlesDatabase
import com.tommyvisic.spaceflightnews.core.data.local.RemoteKeysDao
import com.tommyvisic.spaceflightnews.core.data.remote.SpaceFlightNewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

/**
 * Our Dagger Hilt dependency injection graph spec.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    fun provideSpaceFlightNewsApi(client: OkHttpClient): SpaceFlightNewsApi =
        Retrofit.Builder()
            .baseUrl(SpaceFlightNewsApi.BaseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create()

    @Provides
    @Singleton
    fun provideArticlesDatabase(@ApplicationContext context: Context): ArticlesDatabase =
        Room
            .databaseBuilder(context, ArticlesDatabase::class.java, "articlesDatabase")
            .build()

    @Provides
    @Singleton
    fun provideArticlesDao(articlesDatabase: ArticlesDatabase): ArticlesDao =
        articlesDatabase.getArticlesDao()

    @Provides
    @Singleton
    fun provideRemoteKeysDao(articlesDatabase: ArticlesDatabase): RemoteKeysDao =
        articlesDatabase.getRemoteKeysDao()

    @Provides
    @Singleton
    fun provideArticlesRepository(
        spaceFlightNewsApi: SpaceFlightNewsApi,
        articlesDatabase: ArticlesDatabase
    ): ArticlesRepository = DefaultArticlesRepository(spaceFlightNewsApi, articlesDatabase)
}