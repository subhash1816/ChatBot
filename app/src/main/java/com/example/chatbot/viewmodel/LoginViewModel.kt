package com.example.chatbot.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.ui.login.LoginState
import com.example.chatbot.utils.Constants.Companion.EMAIL_LABEL
import com.example.chatbot.utils.Constants.Companion.ENTER_VALID_EMAIL
import com.example.chatbot.utils.Constants.Companion.ENTER_VALID_PASSWORD
import com.example.chatbot.utils.Constants.Companion.LOGIN_FAILED
import com.example.chatbot.utils.Constants.Companion.PASSWORD_LABEL
import com.example.chatbot.utils.CustomerInfo
import com.example.chatbot.utils.TextFieldValidator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.log

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val email = TextFieldValidator()
    val password = TextFieldValidator()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _loginEvents = MutableSharedFlow<LoginState>()
    val loginEvents = _loginEvents.asSharedFlow()

    init {

        email.label = EMAIL_LABEL
        password.label = PASSWORD_LABEL
        email.errorMessage = ENTER_VALID_EMAIL
        password.errorMessage = ENTER_VALID_PASSWORD


    }

    fun onLoginClick() {

        if (email.isValidEmail().not()) {
            email.setErrorState(true)
        } else if (password.text.isEmpty()) {
            password.setErrorState(true)
        } else {
            viewModelScope.launch {
                _loginState.emit(LoginState.Loading)
            }
            auth.signInWithEmailAndPassword(email.text, password.text)
                .addOnCompleteListener { task ->

                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            CustomerInfo.email = auth.currentUser?.email.toString()
                            _loginEvents.emit(LoginState.Success)
                        } else {

                            _loginState.update {
                                LoginState.Error(
                                    task.exception?.message ?: LOGIN_FAILED
                                )
                            }
                        }
                        delay(2000L)
                        _loginState.update { LoginState.Idle }
                    }
                }
        }
    }



}
