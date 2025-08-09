package com.example.chatbot.ui.login

sealed class LoginState {
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
    data object Loading : LoginState()
    data object Idle : LoginState()

}