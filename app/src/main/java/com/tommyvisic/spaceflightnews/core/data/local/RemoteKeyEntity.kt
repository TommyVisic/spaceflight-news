package com.tommyvisic.spaceflightnews.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A linking table between an article and its related keys. These keys are used by the remote
 * mediator to ensure we make the proper calls to the network to get the right data (driving the
 * limit and offset parameters of the network call).
 */
@Entity(tableName = "remoteKeys")
data class RemoteKeyEntity(
    @PrimaryKey val articleId: Int,
    val previousKey: Int?,
    val currentKey: Int,
    val nextKey: Int?,
    val createdTime: Long = System.currentTimeMillis()
)