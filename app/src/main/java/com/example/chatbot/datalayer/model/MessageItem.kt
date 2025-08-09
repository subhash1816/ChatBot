package com.example.chatbot.datalayer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageItem(
    val message: String,
    val isUser: Boolean,
    val timestamp: Long
): Parcelable
