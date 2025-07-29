package com.abrar.astrolearn.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_topics")
data class FavoriteTopic(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val explanation: String
)
