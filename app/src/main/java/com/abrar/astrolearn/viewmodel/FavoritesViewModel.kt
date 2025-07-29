package com.abrar.astrolearn.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abrar.astrolearn.data.database.AstroDatabase
import com.abrar.astrolearn.data.entity.FavoriteTopic
import com.abrar.astrolearn.data.repository.FavoriteTopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FavoriteTopicRepository
    val allFavorites: Flow<List<FavoriteTopic>>

    init {
        val database = AstroDatabase.getDatabase(application)
        val dao = database.favoriteTopicDao()
        repository = FavoriteTopicRepository(dao)
        allFavorites = repository.getAllFavorites()
    }

    suspend fun addToFavorites(topicName: String, explanation: String): Boolean {
        return try {
            val existingFavorite = repository.getFavoriteByName(topicName)
            if (existingFavorite != null) {
                false // Already exists
            } else {
                val favoriteTopic = FavoriteTopic(
                    name = topicName,
                    explanation = explanation
                )
                repository.insertFavorite(favoriteTopic)
                true // Successfully added
            }
        } catch (e: Exception) {
            false // Error occurred
        }
    }

    fun removeFromFavorites(topic: FavoriteTopic) {
        viewModelScope.launch {
            repository.deleteFavorite(topic)
        }
    }

    suspend fun isTopicFavorite(topicName: String): Boolean {
        return repository.getFavoriteByName(topicName) != null
    }
}
