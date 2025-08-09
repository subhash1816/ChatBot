package com.example.chatbot.ui.register

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatbot.R
import com.example.chatbot.ui.login.LoginState
import com.example.chatbot.ui.utils.ButtonTv
import com.example.chatbot.ui.utils.CustomLinearProgressBar
import com.example.chatbot.ui.utils.CustomToast
import com.example.chatbot.ui.utils.EmailField
import com.example.chatbot.ui.utils.ErrorText
import com.example.chatbot.ui.utils.GenericTv
import com.example.chatbot.ui.utils.LabelTv
import com.example.chatbot.ui.utils.PasswordField
import com.example.chatbot.ui.utils.TextButtonTv
import com.example.chatbot.utils.Constants.Companion.ALREADY_HAVE_ACCOUNT
import com.example.chatbot.utils.Constants.Companion.BUTTON_REGISTER
import com.example.chatbot.utils.Constants.Companion.REGISTER
import com.example.chatbot.viewmodel.RegisterViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {


    val loginState by registerViewModel.loginState.collectAsStateWithLifecycle()
    when (loginState) {
        is LoginState.Loading -> CustomLinearProgressBar()
        is LoginState.Error -> {
            CustomToast(message = (loginState as LoginState.Error).message)
        }
        else -> {}
    }

    LaunchedEffect(Unit) {
        registerViewModel.loginEvents.collect { event ->
            when (event) {
                is LoginState.Success -> onRegisterSuccess()
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
        GenericTv(text = REGISTER)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = registerViewModel.name.text,
            onValueChange = {
                registerViewModel.name.setErrorState(false)
                registerViewModel.name.text = it
                            },
            label = { LabelTv(registerViewModel.name.label) },
            textStyle = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
                color = colorResource(R.color.color_4f3267),
                lineHeight = 16.sp,
                letterSpacing = 0.15.sp,
            ),
            shape = RoundedCornerShape(12.dp),
            supportingText = {
                ErrorText(registerViewModel.name)
            },
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = colorResource(R.color.color_D92D20),
                unfocusedBorderColor = colorResource(R.color.color_4f3267),
                focusedBorderColor = Color.Black,
                errorLabelColor = colorResource(R.color.color_D92D20),
                unfocusedLabelColor = colorResource(R.color.color_4f3267),
                focusedLabelColor = colorResource(R.color.color_4f3267),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedTextColor = colorResource(R.color.color_4f3267),
                unfocusedTextColor = colorResource(R.color.color_4f3267),
                errorTextColor = colorResource(R.color.color_4f3267)
            ),
            isError = registerViewModel.name.getErrorState()
        )

        Spacer(Modifier.height(8.dp))

        EmailField(registerViewModel.email)

        Spacer(Modifier.height(8.dp))

        PasswordField(registerViewModel.password)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = registerViewModel.confirmPassword.text,
            onValueChange = {
                registerViewModel.confirmPassword.setErrorState(false)
                registerViewModel.confirmPassword.text = it
                            },
            label = { LabelTv(registerViewModel.confirmPassword.label) },
           // visualTransformation = PasswordVisualTransformation(),
            textStyle = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
                color = colorResource(R.color.color_4f3267),
                lineHeight = 16.sp,
                letterSpacing = 0.15.sp,
            ),
            shape = RoundedCornerShape(12.dp),
            supportingText = {
                ErrorText(registerViewModel.confirmPassword)
            },
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = colorResource(R.color.color_D92D20),
                unfocusedBorderColor = colorResource(R.color.color_4f3267),
                focusedBorderColor = Color.Black,
                errorLabelColor = colorResource(R.color.color_D92D20),
                unfocusedLabelColor = colorResource(R.color.color_4f3267),
                focusedLabelColor = colorResource(R.color.color_4f3267),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedTextColor = colorResource(R.color.color_4f3267),
                unfocusedTextColor = colorResource(R.color.color_4f3267),
                errorTextColor = colorResource(R.color.color_4f3267)
            ),
            isError = registerViewModel.confirmPassword.getErrorState()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                registerViewModel.onRegisterClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButtonTv(BUTTON_REGISTER)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onNavigateBack) {
            ButtonTv(ALREADY_HAVE_ACCOUNT)
        }
    }
}
