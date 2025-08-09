package com.example.chatbot.datalayer.localroom

import android.util.Log
import com.example.chatbot.datalayer.localroom.CacheDao
import com.example.chatbot.datalayer.localroom.CacheEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository that serves as the single source of truth for data.
 *
 * Logic:
 * - If the network is available, fetch from API, cache to Room, and return.
 * - If offline, show offline screen
 */

class CacheRepo(private val cacheDao: CacheDao) {

    suspend fun saveToCache(msg: String, userId: String, isUser: Boolean) {
        val entity = CacheEntity(
            userId = userId,
            text = msg,
            isUser = isUser
        )
        Log.d("Subhash", "onSendClick: $msg, $userId, $isUser")
        cacheDao.insertMessage(entity)
    }

     fun getFromCache(key: String): Flow<List<CacheEntity>> {
        val cached = cacheDao.getMessagesForUser(key)
        return cached
    }

}