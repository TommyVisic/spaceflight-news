package com.tommyvisic.spaceflightnews.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.tommyvisic.spaceflightnews.R

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    @StringRes val titleStringResource: Int) {

    data object Feed : Screen(
        route = "feed",
        icon = Icons.Rounded.List,
        titleStringResource = R.string.feed
    )

    data object Favorites : Screen(
        route = "favorites",
        icon = Icons.Rounded.Favorite,
        titleStringResource = R.string.favorites
    )
}

val bottomNavigationScreens = listOf(
    Screen.Feed,
    Screen.Favorites
)