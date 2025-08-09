package com.example.chatbot.datalayer.localroom

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chatbot.datalayer.localroom.CacheConstants.Companion.TABLE_NAME

/**
 * Data class representing a cached item in Room.
 *
 * - [id] is the primary key.
 * - [userId] is the foreign key
 */

@Entity(tableName = TABLE_NAME)
data class CacheEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val text: String,
    val isUser: Boolean,
    val msgTimeStamp: Long = System.currentTimeMillis()
)