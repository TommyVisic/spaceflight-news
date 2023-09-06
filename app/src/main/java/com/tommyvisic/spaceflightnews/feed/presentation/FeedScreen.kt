package com.tommyvisic.spaceflightnews.feed.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tommyvisic.spaceflightnews.core.model.Article
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.tommyvisic.spaceflightnews.R
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val articles = viewModel.articles.collectAsLazyPagingItems()
    val loadState = articles.loadState.mediator

    // Subscribe to refresh events coming from the view model.
    LaunchedEffect(viewModel) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                FeedUiEvent.Refresh -> articles.refresh()
            }
        }
    }

    // This column arranges the top app bar plus the articles list.
    Column(Modifier.fillMaxSize()) {
        // This is a single screen app so I'm just putting the top app bar here so I can easily get
        // the click event from the refresh button.
        CenterAlignedTopAppBar(
            title = { Text(text = stringResource(id = R.string.top_bar_title)) },
            actions = {
                IconButton(onClick = { viewModel.onRefresh() }) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(id = R.string.refresh)
                    )
                }
            }
        )

        // Our main list of articles. Most of the UI for this screen is driven by this list. In
        // addition to showing the articles themselves, the list also shows various status messages
        // as items in the list. For example "loading articles" and "error getting articles".
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.fillMaxSize()
        ) {


            // Check whether we're refreshing before emitting article items. Don't show any items
            // while a refresh is underway.
            when (loadState?.refresh) {
                // If we're not loading, show the article items.
                is LoadState.NotLoading -> {
                    items(
                        count = articles.itemCount,
                        key = articles.itemKey { it.id },
                        contentType = articles.itemContentType { "article" }
                    ) {index ->
                        val item = articles[index]
                        item?.let { article ->
                            ArticleCard(
                                article = article,
                                onClick = { launchCustomTab(context, article.url) }
                            )
                        }
                    }
                }
                // If we are loading, show a loading indicator.
                LoadState.Loading -> {
                    item {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.fetching_articles),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator()
                        }
                    }
                }
                // If there's been an error, show a retry button.
                is LoadState.Error -> {
                    item {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.error_getting_articles),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { articles.refresh() }
                            ) {
                                Text(text = stringResource(R.string.try_again))
                            }
                        }
                    }
                }
                else -> Unit
            }

            // Emit UI based on the append load state. This involves adding an item to the end of
            // list to indicate status. You might not ever see this in the UI if the network calls
            // in the data layer are speedy (there's a delay call you can uncomment in
            // ArticlesRemoteMediator to simulate a slow connection).
            when (loadState?.append) {
                // We reached the end of the articles list and want to show a loading indicator.
                LoadState.Loading -> {
                    item {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.fetching_articles),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator()
                        }
                    }
                }
                // We tried to load more items but failed so show an error message and retry button.
                is LoadState.Error -> {
                    item {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.error_getting_articles),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { articles.retry() }
                            ) {
                                Text(text = stringResource(R.string.try_again))
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}

/**
 * A article item in the lazy column. Shows a card with a header image, title, and summary.
 */
@Composable
fun ArticleCard(
    article: Article,
    onClick: (Article) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.clickable { onClick(article) }
    ) {
        ArticleHeaderImage(imageUrl = article.imageUrl)

        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 24.dp
            )
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 8
            )
        }
    }
}

/**
 * Use COIL to load the header image. COIL will handle the network requests plus local caching.
 */
@Composable
fun ArticleHeaderImage(imageUrl: String?) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val imageLoader = rememberAsyncImagePainter(
        model = imageUrl,
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        },
    )
    val isLocalInspection = LocalInspectionMode.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            // Display a progress bar while loading
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(80.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop,
            painter = if (isError.not() && !isLocalInspection) {
                imageLoader
            } else {
                painterResource(R.drawable.ic_placeholder_default)
            },
            contentDescription = null, // decorative image,
        )
    }
}

/**
 * Open a browser tab in the app. This happens when an article is clicked.
 */
fun launchCustomTab(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}
