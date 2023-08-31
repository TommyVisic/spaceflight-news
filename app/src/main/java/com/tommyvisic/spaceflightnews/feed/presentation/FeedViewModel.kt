package com.tommyvisic.spaceflightnews.feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.tommyvisic.spaceflightnews.core.data.ArticlesRepository
import com.tommyvisic.spaceflightnews.core.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The view model that powers the feed screen.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {

    private val _uiEvents = MutableSharedFlow<FeedUiEvent>()

    /**
     * The UI observers this flow of events to respond to the viewModel.
     */
    val uiEvents = _uiEvents.asSharedFlow()
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    /**
     * The UI observes this flow to drive the pagination of the articles list. PagingData also
     * provides load state, which indicates whether we're refreshing or appending articles to the
     * list, as well as error information. In the presentation layer you'll see this object used
     * a lot to get work done.
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