package com.abrar.astrolearn.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.abrar.astrolearn.data.dao.FavoriteTopicDao
import com.abrar.astrolearn.data.entity.FavoriteTopic

@Database(
    entities = [FavoriteTopic::class],
    version = 2,  // Incremented from 1 to 2 to reflect schema changes
    exportSchema = false
)
abstract class AstroDatabase : RoomDatabase() {
    abstract fun favoriteTopicDao(): FavoriteTopicDao

    companion object {
        @Volatile
        private var INSTANCE: AstroDatabase? = null

        // Migration from version 1 to 2 - adds dateAdded column
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add the new dateAdded column with a default value of current timestamp
                db.execSQL(
                    "ALTER TABLE favorite_topics ADD COLUMN dateAdded INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}"
                )
            }
        }

        fun getDatabase(context: Context): AstroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AstroDatabase::class.java,
                    "astro_database"
                )
                .addMigrations(MIGRATION_1_2)  // Add the migration
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
