package com.example.chatbot.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TextFieldValidator {
    var errorMessage = ""
    var label = ""
    var placeholder = ""
    var text: String by mutableStateOf("")
    private var displayErrors: Boolean by mutableStateOf(false)

    fun setErrorState(isError: Boolean) {
        displayErrors = isError
    }

    fun getErrorState() = displayErrors

    fun isValidEmail(): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return text.isNotEmpty() && emailRegex.matches(text)
    }
}