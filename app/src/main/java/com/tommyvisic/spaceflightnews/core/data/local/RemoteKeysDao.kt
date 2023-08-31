package com.tommyvisic.spaceflightnews.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Provides remote key database access.
 */
@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(remoteKeys: List<RemoteKeyEntity>)

    @Query("DELETE FROM remoteKeys")
    suspend fun clear()

    @Query("SELECT * FROM remoteKeys WHERE articleId = :id")
    suspend fun get(id: Int): RemoteKeyEntity

    @Query("SELECT createdTime FROM remoteKeys ORDER BY createdTime DESC LIMIT 1")
    suspend fun getCreatedTime(): Long?
}