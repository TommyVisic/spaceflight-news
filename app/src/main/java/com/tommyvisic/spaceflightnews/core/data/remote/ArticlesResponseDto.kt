package com.tommyvisic.spaceflightnews.core.data.remote

import com.squareup.moshi.Json

/**
 * This data transfer object gets returned from our Space Flight News Retrofit API.
 */
class ArticlesResponseDto(
    @field:Json(name = "results") val articleDtos: List<ArticleDto>
)