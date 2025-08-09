package com.example.chatbot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.ui.login.LoginState
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
        name.label = "Name"
        email.label = "Email"
        password.label = "Password"
        confirmPassword.label = "Confirm Password"

        name.errorMessage = "Please Enter Name"
        email.errorMessage = "Please Enter Email"
        password.errorMessage = "Please Enter Password"
        confirmPassword.errorMessage = "Please Enter Confirm Password"

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
            confirmPassword.errorMessage = "Password and Confirm Password should be same"
        } else {
            viewModelScope.launch {
                _loginState.update { LoginState.Loading }
                try {
                    auth.createUserWithEmailAndPassword(email.text, password.text).await()
                    CustomerInfo.email = auth.currentUser?.email.toString()
                    _loginEvents.emit(LoginState.Success)
                } catch (e: Exception) {
                    _loginState.update { LoginState.Error(e.message ?: "Registration failed") }
                } finally {
                    delay(2000L)
                    _loginState.update { LoginState.Idle }
                }
            }

        }
    }

}