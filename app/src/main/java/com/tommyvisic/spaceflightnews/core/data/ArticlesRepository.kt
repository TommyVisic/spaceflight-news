package com.tommyvisic.spaceflightnews.core.data

import androidx.paging.PagingData
import com.tommyvisic.spaceflightnews.core.model.Article
import kotlinx.coroutines.flow.Flow

/**
 * Check out DefaultArticlesRepository for the concrete type that the app uses.
 */
interface ArticlesRepository {
    suspend fun getArticles(limit: Int, offset: Int): Result<List<Article>>

    fun observeArticles(): Flow<List<Article>>

    fun getArticles(): Flow<PagingData<Article>>
}