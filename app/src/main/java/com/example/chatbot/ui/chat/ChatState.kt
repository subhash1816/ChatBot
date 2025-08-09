package com.example.chatbot.ui.chat

sealed class ChatState {
    data object Success: ChatState()
    data object Initial: ChatState()
    data object HideProgress: ChatState()
    data object ShowProgress: ChatState()
    data object Empty: ChatState()
    data object Idle: ChatState()
    data class OpenWebView(val url: String?): ChatState()

    data class Failure(val msg: String?): ChatState()
}