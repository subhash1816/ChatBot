package com.example.chatbot.datalayer.localroom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chatbot.datalayer.localroom.CacheConstants.Companion.TABLE_NAME
import com.example.chatbot.datalayer.model.MessageItem
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing the cached items table.
 *
 * - [getCacheByKey] returns cached item if available.
 * - [insertCache] replaces old cache on refresh.
 */

@Dao
interface CacheDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE userId = :userId ORDER BY msgTimeStamp ASC")
    fun getMessagesForUser(userId: String): Flow<List<CacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: CacheEntity)

}