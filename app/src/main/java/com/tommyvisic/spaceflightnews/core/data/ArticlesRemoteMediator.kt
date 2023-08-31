package com.tommyvisic.spaceflightnews.core.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.tommyvisic.spaceflightnews.core.data.local.ArticleEntity
import com.tommyvisic.spaceflightnews.core.data.local.ArticlesDatabase
import com.tommyvisic.spaceflightnews.core.data.local.RemoteKeyEntity
import com.tommyvisic.spaceflightnews.core.data.local.toEntity
import com.tommyvisic.spaceflightnews.core.data.remote.SpaceFlightNewsApi
import com.tommyvisic.spaceflightnews.core.data.remote.toArticle
import okio.IOException
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

/**
 * This type handles fetching fresh data from the network when 1) our cache is old (as specified by
 * the initialize() method, 2) we run our of cached content and need more, or 3) when a manual
 * refresh is triggered. This type is also responsible for writing the fresh data to the local
 * database.
 */
@OptIn(ExperimentalPagingApi::class)
class ArticlesRemoteMediator(
    private val spaceFlightNewsApi: SpaceFlightNewsApi,
    private val articlesDatabase: ArticlesDatabase
) : RemoteMediator<Int, ArticleEntity>() {

    companion object {
        const val LogTag = "ArticlesRemoteMediator"
    }

    /**
     * Called when we initialize and determines whether we should invalidate the cache based on how
     * old it is. I'm invalidating after 30 minutes here.
     */
    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES)
        val createdTime = articlesDatabase.getRemoteKeysDao().getCreatedTime() ?: 0

        val elapsedTime = System.currentTimeMillis() - createdTime
        Log.d(LogTag, "Created time is $createdTime")

        return if (elapsedTime < cacheTimeout) {
            Log.d(LogTag, "Skipping initial refresh because cache hasn't timed out yet.")
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            Log.d(LogTag, "Performing initial refresh")
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    /**
     * Called when we need more fresh data from the network. LoadType indicates the reason why we
     * need the data.
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        val page = when (loadType) {
            // Just load the first page on a refresh.
            LoadType.REFRESH -> 1
            // In the example I'm following prepending never happens because a refresh always starts
            // us from the beginning.
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                val nextKey = remoteKey?.nextKey
                nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
            }
        }

        // Simulate some high latency action. Uncommenting this will let you see how the app
        // responds to a super slow network connection.
//        delay(5000)

        try {
            // Here's where we translate pages to offset/limit.
            val limit = state.config.pageSize
            val offset = (page - 1) * limit

            Log.d(LogTag, "Fetching articles from Space Flight API with limit $limit and offset $offset")
            val response = spaceFlightNewsApi.getArticles(limit, offset)
            val articles = response.articleDtos.map { it.toArticle() }

            val endOfPaginationReached = articles.isEmpty()

            articlesDatabase.withTransaction {

                // If we're refreshing (as opposed to just appending), then clear the databases.
                // We're starting fresh.
                if (loadType == LoadType.REFRESH) {
                    Log.d(LogTag, "Clearing articles database.")
                    articlesDatabase.getArticlesDao().clear()
                    articlesDatabase.getRemoteKeysDao().clear()
                }

                val previousKey = if (page > 1) page - 1 else null
                val nextKey = if (!endOfPaginationReached) page + 1 else null

                val remoteKeyEntities = articles.map {
                    RemoteKeyEntity(
                        articleId = it.id,
                        previousKey = previousKey,
                        currentKey = page,
                        nextKey = nextKey
                    )
                }

                // Write to our database.
                articlesDatabase.getRemoteKeysDao().insert(remoteKeyEntities)
                articlesDatabase.getArticlesDao().insert(articles.map { it.toEntity() })
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch(e: IOException) {
            return MediatorResult.Error(e)
        } catch(e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ArticleEntity>) =
        state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { article ->
            articlesDatabase.getRemoteKeysDao().get(article.id)
        }
}