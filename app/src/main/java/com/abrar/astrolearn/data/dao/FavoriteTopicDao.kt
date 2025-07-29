package com.abrar.astrolearn.data.dao

import androidx.room.*
import com.abrar.astrolearn.data.entity.FavoriteTopic
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTopicDao {
    @Insert
    suspend fun insert(topic: FavoriteTopic)

    @Delete
    suspend fun delete(topic: FavoriteTopic)

    @Query("SELECT * FROM favorite_topics ORDER BY name ASC")
    fun getAllFavorites(): Flow<List<FavoriteTopic>>

    @Query("SELECT * FROM favorite_topics WHERE name = :topicName LIMIT 1")
    suspend fun getFavoriteByName(topicName: String): FavoriteTopic?
}
