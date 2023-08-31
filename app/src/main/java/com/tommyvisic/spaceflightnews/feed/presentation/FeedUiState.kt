package com.tommyvisic.spaceflightnews.feed.presentation

import com.tommyvisic.spaceflightnews.core.model.Article

/**
 * This isn't used anymore but keeping it around as notes. When I migrated to the Paging3
 * architecture this type became unnecessary.
 */
sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Success(
        val articles: List<Article>
    ) : FeedUiState
    data object Error : FeedUiState
}