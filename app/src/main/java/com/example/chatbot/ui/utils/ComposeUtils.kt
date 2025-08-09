package com.example.chatbot.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.chatbot.R
import com.example.chatbot.utils.Constants.Companion.NO_NETWORK
import com.example.chatbot.utils.Constants.Companion.RETRY
import com.example.chatbot.utils.TextFieldValidator
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.sin

@Composable
fun CustomLinearProgressBar() {
    Dialog(
        onDismissRequest = {},
        DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 30.dp)
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = colorResource(id = R.color.color_8863fb)
            )
        }
    }
}



@Composable
fun CustomToast(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    message: String,
    duration: Long = 2000L, // 2 seconds
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
        delay(duration)
        isVisible = false
    }

    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .then(modifier)
        ) {
            Text(
                text = message,
                color = colorResource(R.color.color_4f3267),
                modifier = Modifier
                    .background(
                        color = if (isError) colorResource(R.color.color_FBA6AD) else colorResource(
                            R.color.color_BAF59C
                        ),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(16.dp),
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
                letterSpacing = 0.15.sp,
                fontSize = 14.sp

            )
        }
    }
}

@Composable
fun NoNetworkDialog(onRetryClick: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GenericTv(NO_NETWORK)
        NoNetworkButtonComponent(text = RETRY) {
            onRetryClick()
        }
    }

}

@Composable
private fun NoNetworkButtonComponent(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        onClick = { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Unspecified,
            disabledContentColor = Color.Unspecified,
            disabledContainerColor = Color.Unspecified,
            containerColor = colorResource(R.color.color_EAE3FF)
        ),
    ) {
        Text(
            modifier = Modifier,
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.W600,
            color = colorResource(R.color.color_4f3267),
            lineHeight = 19.sp,
            letterSpacing = 0.15.sp
        )
    }
}

@Composable
fun GenericTv(text: String) {
    Text(
        modifier = Modifier,
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.W600,
        color = colorResource(R.color.color_4f3267),
        lineHeight = 16.sp,
        letterSpacing = 0.15.sp,
    )
}

@Composable
fun PasswordField(validator: TextFieldValidator) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = validator.text,
        onValueChange = {
            validator.setErrorState(false)
            validator.text = it
        },
        label = {
            LabelTv(validator.label)
        },
        textStyle = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
            color = colorResource(R.color.color_4f3267),
            lineHeight = 16.sp,
            letterSpacing = 0.15.sp,
        ),
        supportingText = {
            ErrorText(validator)
        },
        singleLine = true,
        maxLines = 1,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(12.dp),
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
        isError = validator.getErrorState(),
        trailingIcon = {
            val image = if (passwordVisible) {
                Icons.Default.Lock
            } else {
                Icons.Default.Lock
            }

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
            }
        }
    )
}

@Composable
fun LabelTv(text: String) {
    Text(
        modifier = Modifier,
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.W600,
        color = colorResource(R.color.color_4f3267),
        lineHeight = 16.sp,
        letterSpacing = 0.15.sp,
    )
}

@Composable
fun ButtonTv(text: String) {
    Text(
        modifier = Modifier,
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.W600,
        color = colorResource(R.color.color_4f3267),
        lineHeight = 16.sp,
        letterSpacing = 0.15.sp,
    )
}

@Composable
fun TextButtonTv(text: String) {
    Text(
        modifier = Modifier,
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.W600,
        color = Color.White,
        lineHeight = 16.sp,
        letterSpacing = 0.15.sp,
    )
}

@Composable
fun EmailField(validator: TextFieldValidator) {

    OutlinedTextField(
        value = validator.text,
        onValueChange = {
            validator.setErrorState(false)
            validator.text = it
        },
        label = {
            LabelTv(validator.label)
        },
        textStyle = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
            color = colorResource(R.color.color_4f3267),
            lineHeight = 16.sp,
            letterSpacing = 0.15.sp,
        ),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = VisualTransformation.None,
        supportingText = {
           ErrorText(validator)
        },
        singleLine = true,
        maxLines = 1,
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
            errorTextColor = colorResource(R.color.color_4f3267),
            errorSupportingTextColor = colorResource(R.color.color_D92D20),
            unfocusedSupportingTextColor = colorResource(R.color.color_4f3267),
            focusedSupportingTextColor = colorResource(R.color.color_4f3267)
        ),
        isError = validator.getErrorState()

        )
}

@Composable
fun ErrorText(textFieldValidator: TextFieldValidator) {
    if (textFieldValidator.getErrorState()) {
        Text(
            modifier = Modifier,
            text = textFieldValidator.errorMessage,
            fontSize = 10.sp,
            fontWeight = FontWeight.W600,
            lineHeight = 16.sp,
            letterSpacing = 0.15.sp,
        )
    }
}