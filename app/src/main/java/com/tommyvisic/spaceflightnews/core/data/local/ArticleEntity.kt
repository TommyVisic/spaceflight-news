package com.tommyvisic.spaceflightnews.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tommyvisic.spaceflightnews.core.model.Article

/**
 * The data layer article object that gets read and written to the database.
 */
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: Int,
    val url: String,
    val title: String,
    val summary: String,
    val publishedTime: String,
    val imageUrl: String?
)

/**
 * Map an article entity to a domain layer article.
 */
fun ArticleEntity.ToArticle() =
    Article(
        id = id,
        url = url,
        title = title,
        summary = summary,
        publishedTime = publishedTime,
        imageUrl = imageUrl
    )

/**
 * Map a domain layer article to a data layer entity.
 */
fun Article.toEntity() =
    ArticleEntity(
        id = id,
        url = url,
        title = title,
        summary = summary,
        publishedTime = publishedTime,
        imageUrl = imageUrl
    )