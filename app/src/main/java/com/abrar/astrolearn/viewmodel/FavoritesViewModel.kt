package com.abrar.astrolearn.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abrar.astrolearn.data.database.AstroDatabase
import com.abrar.astrolearn.data.entity.FavoriteTopic
import com.abrar.astrolearn.data.repository.FavoriteTopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class SortOption {
    NAME_ASC, NAME_DESC, DATE_ASC, DATE_DESC
}

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FavoriteTopicRepository

    // Search and sorting state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.DATE_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Recently deleted for undo functionality
    private val _recentlyDeleted = MutableStateFlow<FavoriteTopic?>(null)
    val recentlyDeleted: StateFlow<FavoriteTopic?> = _recentlyDeleted.asStateFlow()

    // All favorites flow - initialized in init block
    private val allFavoritesFlow: Flow<List<FavoriteTopic>>

    init {
        val database = AstroDatabase.getDatabase(application)
        val dao = database.favoriteTopicDao()
        repository = FavoriteTopicRepository(dao)
        allFavoritesFlow = repository.getAllFavorites()
    }

    // Combined flow for filtered and sorted favorites
    val favorites: Flow<List<FavoriteTopic>> = combine(
        allFavoritesFlow,
        searchQuery,
        sortOption
    ) { favorites, query, sort ->
        val filtered = if (query.isBlank()) {
            favorites
        } else {
            favorites.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.explanation.contains(query, ignoreCase = true)
            }
        }

        when (sort) {
            SortOption.NAME_ASC -> filtered.sortedBy { it.name }
            SortOption.NAME_DESC -> filtered.sortedByDescending { it.name }
            SortOption.DATE_ASC -> filtered.sortedBy { it.dateAdded }
            SortOption.DATE_DESC -> filtered.sortedByDescending { it.dateAdded }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            // Simulate refresh delay
            kotlinx.coroutines.delay(500)
            _isRefreshing.value = false
        }
    }

    suspend fun addToFavorites(topicName: String, explanation: String): Boolean {
        return try {
            val existingFavorite = repository.getFavoriteByName(topicName)
            if (existingFavorite != null) {
                false // Already exists
            } else {
                val favoriteTopic = FavoriteTopic(
                    name = topicName,
                    explanation = explanation,
                    dateAdded = System.currentTimeMillis()
                )
                repository.insertFavorite(favoriteTopic)
                true // Successfully added
            }
        } catch (_: Exception) {
            false // Error occurred
        }
    }

    fun removeFromFavorites(topic: FavoriteTopic) {
        viewModelScope.launch {
            _recentlyDeleted.value = topic
            repository.deleteFavorite(topic)
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            _recentlyDeleted.value?.let { topic ->
                repository.insertFavorite(topic)
                _recentlyDeleted.value = null
            }
        }
    }

    fun clearRecentlyDeleted() {
        _recentlyDeleted.value = null
    }
}
