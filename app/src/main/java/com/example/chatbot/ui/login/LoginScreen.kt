package com.example.chatbot.ui.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatbot.ui.utils.ButtonTv
import com.example.chatbot.ui.utils.CustomLinearProgressBar
import com.example.chatbot.ui.utils.CustomToast
import com.example.chatbot.ui.utils.CustomToastManager
import com.example.chatbot.ui.utils.EmailField
import com.example.chatbot.ui.utils.GenericTv
import com.example.chatbot.ui.utils.PasswordField
import com.example.chatbot.ui.utils.TextButtonTv
import com.example.chatbot.viewmodel.LoginViewModel
import kotlin.math.log

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()

    when (loginState) {
        is LoginState.Loading -> CustomLinearProgressBar()
        is LoginState.Error -> {
            Log.d("Subhash", "LoginScreen:Errpr ${(loginState as LoginState.Error).message} ")
            CustomToast(message = (loginState as LoginState.Error).message)
        }

        else -> {}
    }


    LaunchedEffect(Unit) {
        loginViewModel.loginEvents.collect { event ->
            when (event) {
                is LoginState.Success -> onLoginSuccess()
                else -> {}
            }
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GenericTv(text = "Login")
        Spacer(Modifier.height(16.dp))
        EmailField(loginViewModel.email)
        Spacer(Modifier.height(8.dp))
        PasswordField(loginViewModel.password)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                loginViewModel.onLoginClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButtonTv("Login")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onNavigateToRegister) {
            ButtonTv("Don't have an account? Register")
        }
    }
}



