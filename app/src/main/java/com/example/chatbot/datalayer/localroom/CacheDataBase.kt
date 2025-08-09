package com.example.chatbot.datalayer.localroom

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.example.chatbot.datalayer.localroom.CacheConstants.Companion.CACHE_DATABASE

@Database(entities = [CacheEntity::class], version = 1, exportSchema = false)
abstract class CacheDataBase: RoomDatabase() {
    abstract fun cacheDao(): CacheDao

    companion object {
        @Volatile
        private var INSTANCE: CacheDataBase? = null

        fun getInstance(context: Context): CacheDataBase {

            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    CacheDataBase::class.java,
                    CACHE_DATABASE
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}