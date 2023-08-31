package com.tommyvisic.spaceflightnews.feed.presentation

/**
 * Models an event coming from the view model that the UI can respond to.
 */
sealed interface FeedUiEvent {
    data object Refresh : FeedUiEvent
}