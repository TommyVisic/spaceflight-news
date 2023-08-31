package com.tommyvisic.spaceflightnews.core.model

/**
 * The domain layer article model.
 */
data class Article(
    val id: Int,
    val url: String,
    val title: String,
    val summary: String,
    val publishedTime: String, // <-- This is going to be an actual time type later
    val imageUrl: String?
)
