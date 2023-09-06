package com.tommyvisic.spaceflightnews.core.presentation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

/**
 * The top bar of the app is built using an object of this type. It's provided by a callback passed
 * into the individual screens.
 */
data class TopBarConfig(
    val title: @Composable () -> Unit = {},
    val actions: @Composable RowScope.() -> Unit = {}
)

