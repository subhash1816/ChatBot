package com.example.chatbot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.ui.login.LoginState
import com.example.chatbot.utils.Constants.Companion.CONFIRM_PASSWORD_LABEL
import com.example.chatbot.utils.Constants.Companion.EMAIL_LABEL
import com.example.chatbot.utils.Constants.Companion.ENTER_CONFIRM_PASSWORD
import com.example.chatbot.utils.Constants.Companion.ENTER_VALID_EMAIL
import com.example.chatbot.utils.Constants.Companion.ENTER_VALID_NAME
import com.example.chatbot.utils.Constants.Companion.ENTER_VALID_PASSWORD
import com.example.chatbot.utils.Constants.Companion.NAME_LABEL
import com.example.chatbot.utils.Constants.Companion.PASSWORD_LABEL
import com.example.chatbot.utils.Constants.Companion.PASSWORD_MISMATCH
import com.example.chatbot.utils.Constants.Companion.REGISTRATION_FAILED
import com.example.chatbot.utils.CustomerInfo
import com.example.chatbot.utils.TextFieldValidator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    val name = TextFieldValidator()
    val email = TextFieldValidator()
    val password = TextFieldValidator()
    val confirmPassword = TextFieldValidator()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _loginEvents = MutableSharedFlow<LoginState>()
    val loginEvents = _loginEvents.asSharedFlow()


    init {
        name.label = NAME_LABEL
        email.label = EMAIL_LABEL
        password.label = PASSWORD_LABEL
        confirmPassword.label = CONFIRM_PASSWORD_LABEL

        name.errorMessage =  ENTER_VALID_NAME
        email.errorMessage = ENTER_VALID_EMAIL
        password.errorMessage = ENTER_VALID_PASSWORD
        confirmPassword.errorMessage = ENTER_CONFIRM_PASSWORD

    }

    fun onRegisterClick() {
        if (name.text.isEmpty()) {
            name.setErrorState(true)
        } else if (email.isValidEmail().not()) {
            email.setErrorState(true)
        } else if (password.text.isEmpty()) {
            password.setErrorState(true)
        } else if (confirmPassword.text.isEmpty()) {
            confirmPassword.setErrorState(true)
        } else if (password.text != confirmPassword.text) {
            confirmPassword.setErrorState(true)
            confirmPassword.errorMessage = PASSWORD_MISMATCH
        } else {
            viewModelScope.launch {
                _loginState.update { LoginState.Loading }
                try {
                    auth.createUserWithEmailAndPassword(email.text, password.text).await()
                    CustomerInfo.email = auth.currentUser?.email.toString()
                    _loginEvents.emit(LoginState.Success)
                } catch (e: Exception) {
                    _loginState.update { LoginState.Error(e.message ?: REGISTRATION_FAILED) }
                } finally {
                    delay(2000L)
                    _loginState.update { LoginState.Idle }
                }
            }

        }
    }

}