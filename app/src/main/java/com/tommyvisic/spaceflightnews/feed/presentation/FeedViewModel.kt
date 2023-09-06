package com.tommyvisic.spaceflightnews.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.tommyvisic.spaceflightnews.core.data.ArticlesRepository
import com.tommyvisic.spaceflightnews.core.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The view model that powers the feed screen.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {

    companion object {
        const val LogTag = "FeedViewModel"
    }

    /**
     * The UI observes this flow of events to respond to the view model.
     */
    private val _uiEvents = MutableSharedFlow<FeedUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    /**
     * The UI observes this flow to drive the pagination of the articles list. PagingData also
     * provides load state, which indicates whether we're refreshing or appending articles to the
     * list, as well as error information. In FeedScreen you'll see this object used a lot to get
     * work done.
     */
    var articles: Flow<PagingData<Article>> = articlesRepository.getArticles()

    /**
     * Called when the refresh button in the top app bar is clicked.
     */
    fun onRefresh() {
        viewModelScope.launch {
            _uiEvents.emit(FeedUiEvent.Refresh)
        }
    }
}