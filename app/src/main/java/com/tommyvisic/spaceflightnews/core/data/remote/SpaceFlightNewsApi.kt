package com.tommyvisic.spaceflightnews.core.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Our Space Flight News Retrofit API. A concrete implementation of this interface is created as
 * part of dependency injection within the AppModule type.
 */
interface SpaceFlightNewsApi {

    @GET("v4/articles/?format=json")
    suspend fun getArticles(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ArticlesResponseDto

    companion object {
        const val BaseUrl = "https://api.spaceflightnewsapi.net"
    }
}
