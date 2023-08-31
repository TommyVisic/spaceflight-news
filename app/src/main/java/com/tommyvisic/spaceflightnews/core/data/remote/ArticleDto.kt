package com.tommyvisic.spaceflightnews.core.data.remote

import com.squareup.moshi.Json
import com.tommyvisic.spaceflightnews.core.model.Article

/**
 * The data transfer object for an article. Retrofit will populate these objects as part of calling
 * the Space Flight News API.
 */
data class ArticleDto(
    val id: Int,
    val url: String,
    val title: String,
    val summary: String,
    @field:Json(name = "published_at")
    val publishedTime: String,
    @field:Json(name = "image_url")
    val imageUrl: String?
)

/**
 * Map an article data transfer object to an domain layer article.
 */
fun ArticleDto.toArticle(): Article =
    Article(
        id = id,
        url = url,
        title = title,
        summary = summary,
        publishedTime = publishedTime,
        imageUrl = imageUrl
    )