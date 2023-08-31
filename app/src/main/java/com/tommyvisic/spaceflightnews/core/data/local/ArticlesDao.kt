package com.tommyvisic.spaceflightnews.core.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Provides articles database access including the paging source.
 */
@Dao
interface ArticlesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun clear()

    /**
     * The return type of PagingSource here triggers a lot of behind the scenes magic around Room
     * and Paging3. The PagingSource is backed by the local database. This object gets tied into the
     * whole process in DefaultArticlesRepository where the Pager object is built.
     */
    @Query("SELECT * FROM articles ORDER BY publishedTime DESC")
    fun pagingSource(): PagingSource<Int, ArticleEntity>
}