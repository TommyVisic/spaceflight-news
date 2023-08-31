package com.tommyvisic.spaceflightnews.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Our Room database spec.
 */
@Database(
    entities = [ArticleEntity::class, RemoteKeyEntity::class],
    version = 1
)
abstract class ArticlesDatabase : RoomDatabase() {
    abstract fun getArticlesDao(): ArticlesDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao
}