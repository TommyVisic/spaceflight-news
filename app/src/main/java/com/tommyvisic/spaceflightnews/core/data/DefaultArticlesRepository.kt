package com.tommyvisic.spaceflightnews.core.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.tommyvisic.spaceflightnews.core.data.local.ArticlesDatabase
import com.tommyvisic.spaceflightnews.core.data.local.ToArticle
import com.tommyvisic.spaceflightnews.core.data.remote.SpaceflightNewsApi
import com.tommyvisic.spaceflightnews.core.data.remote.toArticle
import com.tommyvisic.spaceflightnews.core.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Our default implementation for the articles repository.
 */
class DefaultArticlesRepository @Inject constructor (
    private val spaceflightNewsApi: SpaceflightNewsApi,
    private val articlesDatabase: ArticlesDatabase
) : ArticlesRepository {

    companion object {
        const val PageSize = 10
    }

    /**
     * The remote mediator uses this method to get fresh articles from the network.
     */
    override suspend fun getArticles(limit: Int, offset: Int): Result<List<Article>> =
        try {
            val response = spaceflightNewsApi.getArticles(limit, offset)
            Result.success(response.articleDtos.map { it.toArticle() })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }

    /**
     * This isn't in use but I'm keeping this around as an example of how to adapt a Retrofit API
     * to a Kotlin Flow.
     */
    override fun observeArticles(): Flow<List<Article>> = flow {
        try {
            val response = spaceflightNewsApi.getArticles(10, 0)
            emit(response.articleDtos.map { it.toArticle() })
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    /**
     * This is where we build the pager and associate the config, paging source (from the articles
     * DAO), and the articles remote mediator. The presentation layer ends up observing this pager
     * to power the articles feed.
     */
    @OptIn(ExperimentalPagingApi::class)
    override fun getArticles(): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = PageSize,
            prefetchDistance = 2, // <-- Keeping this low to test out the loading UI
            initialLoadSize = PageSize
        ),
        pagingSourceFactory = {
            articlesDatabase.getArticlesDao().pagingSource()
        },
        remoteMediator = ArticlesRemoteMediator(
            spaceflightNewsApi = spaceflightNewsApi,
            articlesDatabase = articlesDatabase
        )
    ).flow.map { pagingData -> pagingData.map { it.ToArticle() } } // <-- Map from entity to domain model
}