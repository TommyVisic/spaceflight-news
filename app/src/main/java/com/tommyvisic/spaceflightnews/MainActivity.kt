package com.tommyvisic.spaceflightnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tommyvisic.spaceflightnews.feed.presentation.FeedScreen
import com.tommyvisic.spaceflightnews.ui.theme.SpaceFlightNewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpaceFlightNewsTheme {
                val topBarActionsState = remember { mutableStateOf<@Composable RowScope.() -> Unit>({ }) }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(text = stringResource(id = R.string.top_bar_title)) },
                            actions = topBarActionsState.value
                        )
                    }
                ) { innerPadding ->
                    FeedScreen(
                        topBarActionsState,
                        Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
