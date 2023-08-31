package com.tommyvisic.spaceflightnews.core.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tommyvisic.spaceflightnews.core.data.remote.SpaceFlightNewsApi
import com.tommyvisic.spaceflightnews.core.data.remote.toArticle
import com.tommyvisic.spaceflightnews.core.model.Article

/**
 * We're not using this type anymore. I used this to get basic, network-only pagination working
 * before learning about  local database caching. Keeping this around as an example.
 */
class RemoteArticlesPagingSource(
    private val spaceFlightNewsApi: SpaceFlightNewsApi
) : PagingSource<Int, Article>() {

    companion object {
        const val PageSize = 10
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        val refreshKey =  state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

        Log.d("ArticlesPagingSource", "refreshKey = $refreshKey")

        return refreshKey
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> =
        try {
            val page = params.key ?: 1

            val limit = params.loadSize
            val offset = (page - 1) * params.loadSize
            Log.d("ArticlesPagingSource", "key = ${params.key},  page = $page, limit = $limit, offset = $offset")
            val response = spaceFlightNewsApi.getArticles(limit, offset)
            val articles = response.articleDtos.map { it.toArticle() }

            LoadResult.Page(
                data = articles,
                prevKey = if (page - 1 > 0) page - 1 else null,
                nextKey = if (articles.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
}