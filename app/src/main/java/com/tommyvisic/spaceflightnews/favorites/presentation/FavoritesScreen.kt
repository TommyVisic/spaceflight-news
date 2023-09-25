package com.tommyvisic.spaceflightnews.favorites.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tommyvisic.spaceflightnews.R
import com.tommyvisic.spaceflightnews.core.presentation.TopBarConfig

@Composable
fun FavoritesScreen(
    onCreateTopBarConfig: (TopBarConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onCreateTopBarConfig(
            TopBarConfig(
                title = { Text(text = stringResource(id = R.string.favorites)) },
                actions = {}
            )
        )
    }

    Box(modifier = modifier.fillMaxSize()) {

    }
}
