package com.abrar.astrolearn.data.repository

import com.abrar.astrolearn.data.dao.FavoriteTopicDao
import com.abrar.astrolearn.data.entity.FavoriteTopic
import kotlinx.coroutines.flow.Flow

class FavoriteTopicRepository(private val favoriteTopicDao: FavoriteTopicDao) {

    fun getAllFavorites(): Flow<List<FavoriteTopic>> = favoriteTopicDao.getAllFavorites()

    suspend fun insertFavorite(topic: FavoriteTopic) {
        favoriteTopicDao.insert(topic)
    }

    suspend fun deleteFavorite(topic: FavoriteTopic) {
        favoriteTopicDao.delete(topic)
    }

    suspend fun getFavoriteByName(topicName: String): FavoriteTopic? {
        return favoriteTopicDao.getFavoriteByName(topicName)
    }
}
