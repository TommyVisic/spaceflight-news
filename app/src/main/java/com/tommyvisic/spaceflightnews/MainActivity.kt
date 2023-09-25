package com.tommyvisic.spaceflightnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tommyvisic.spaceflightnews.core.navigation.Screen
import com.tommyvisic.spaceflightnews.core.navigation.bottomNavigationScreens
import com.tommyvisic.spaceflightnews.core.presentation.TopBarConfig
import com.tommyvisic.spaceflightnews.favorites.presentation.FavoritesScreen
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
                val navController = rememberNavController()

                // The top bar config used below to configure the top bar. This is set by a callback
                // provided to the individual screens.
                var topBarConfig by remember { mutableStateOf(TopBarConfig()) }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title =  {
                                // Fade in/out changes made to the title.
                                Crossfade(targetState = topBarConfig.title, label = "") {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        it()
                                    }
                                }
                            },
                            actions = {
                                // Fade in/out changes made to the actions.
                                Crossfade(targetState = topBarConfig.actions, label = "") {
                                    it(this)
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavigation(
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            elevation = 8.dp
                        ) {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            bottomNavigationScreens.forEach { screen ->
                                BottomNavigationItem(
                                    selected = currentDestination?.route == screen.route,
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = stringResource(
                                                screen.titleStringResource
                                            )
                                        )
                                    },
                                    label = { Text(stringResource(screen.titleStringResource)) },
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )

                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Feed.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Feed.route) {
                            FeedScreen(
                                onCreateTopBarConfig = { topBarConfig = it }
                            )
                        }
                        composable(Screen.Favorites.route) {
                            FavoritesScreen(
                                onCreateTopBarConfig = { topBarConfig = it }
                            )
                        }
                    }
                }
            }
        }
    }
}
