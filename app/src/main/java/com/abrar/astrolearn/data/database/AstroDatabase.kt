package com.abrar.astrolearn.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.abrar.astrolearn.data.dao.FavoriteTopicDao
import com.abrar.astrolearn.data.entity.FavoriteTopic

@Database(
    entities = [FavoriteTopic::class],
    version = 1,
    exportSchema = false
)
abstract class AstroDatabase : RoomDatabase() {
    abstract fun favoriteTopicDao(): FavoriteTopicDao

    companion object {
        @Volatile
        private var INSTANCE: AstroDatabase? = null

        fun getDatabase(context: Context): AstroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AstroDatabase::class.java,
                    "astro_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
