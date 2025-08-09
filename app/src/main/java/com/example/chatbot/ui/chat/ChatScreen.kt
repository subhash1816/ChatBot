package com.example.chatbot.ui.chat

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.example.chatbot.datalayer.model.MessageItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.chatbot.R
import com.example.chatbot.ui.navigation.NavRoutes
import com.example.chatbot.ui.utils.CustomToast
import com.example.chatbot.utils.Constants.Companion.CHATBOT_NAME
import com.example.chatbot.utils.Constants.Companion.PERMISSION_DENIED
import com.example.chatbot.utils.Constants.Companion.SIGNOUT
import com.example.chatbot.utils.TextFieldValidator
import com.example.chatbot.utils.formatTimestamp
import com.example.chatbot.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun BubbleMessage(message: MessageItem) {
    val bubbleColor = if (message.isUser) Color(0xFFDCF8C6) else Color(0xFFFFFFFF)
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bubbleShape = if (message.isUser) {
        RoundedCornerShape(12.dp, 0.dp, 12.dp, 12.dp)
    } else {
        RoundedCornerShape(0.dp, 12.dp, 12.dp, 12.dp)
    }

    Column(
        horizontalAlignment = alignment,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(bubbleColor, bubbleShape)
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.message,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                color = colorResource(R.color.color_4f3267),
                letterSpacing = 0.2.sp
            )
        }
        Text(
            text = formatTimestamp(message.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
        )
    }
}

@Composable
fun ChatScreen(chatViewModel: ChatViewModel, navController: NavHostController) {
    val listState = rememberLazyListState()
    val state by chatViewModel.chatState.collectAsStateWithLifecycle()
    val recordAudioPermission = Manifest.permission.RECORD_AUDIO
    val context = LocalContext.current
    val messages by chatViewModel.messages.collectAsStateWithLifecycle()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            chatViewModel.startService()
        } else {
            Toast.makeText(context, PERMISSION_DENIED, Toast.LENGTH_SHORT).show()
        }
    }
    when (state) {
        is ChatState.Failure -> {
            CustomToast(message = (state as ChatState.Failure).msg.toString())
        }

        else -> {}
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            TopSection {
                chatViewModel.signout()
                navController.navigate(NavRoutes.LOGIN.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        },
        bottomBar = {
            QueryField(
                chatViewModel.queryField,
                isListening = chatViewModel.isListening,
                onSendClick = { chatViewModel.onSendClick() },
                onSpeechClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            recordAudioPermission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        chatViewModel.startService()

                    } else {
                        permissionLauncher.launch(recordAudioPermission)
                    }
                })
        },
    ) { paddingValues ->

        LaunchedEffect(Unit) {
            snapshotFlow { messages.size }
                .distinctUntilChanged()
                .collect { size ->
                    if (size > 0) {
                        listState.animateScrollToItem(messages.size - 1)
                    }
                }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            state = listState

        ) {
            items(messages.size) { index ->
                BubbleMessage(
                    MessageItem(
                        message = messages[index].text,
                        isUser = messages[index].isUser,
                        timestamp = messages[index].msgTimeStamp
                    )
                )
            }
        }
    }

}

@Composable
fun TopSection(onSignOut: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Image(painter = painterResource(R.drawable.chatbot), contentDescription = "chatbot")
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = CHATBOT_NAME,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                color = colorResource(R.color.color_4f3267),
                letterSpacing = 0.2.sp
            )
        }
        Row {
            TextButton(onClick = { onSignOut() }) {
                Text(
                    text = SIGNOUT,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W600,
                    color = colorResource(R.color.color_8863fb),
                    letterSpacing = 0.2.sp
                )
            }

        }

    }
}



@Composable
fun QueryField(
    textFieldValidator: TextFieldValidator,
    isListening: Boolean,
    onSendClick: () -> Unit,
    onSpeechClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val infiniteTransition = rememberInfiniteTransition()

    // Animation for pulsing mic while listening
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .imePadding()
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 12.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            colorResource(R.color.color_E56EEB),
                            colorResource(R.color.color_8863fb)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            value = textFieldValidator.text,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = colorResource(R.color.color_4f3267),
                lineHeight = 16.sp,
                letterSpacing = 0.15.sp,
            ),
            onValueChange = {
                textFieldValidator.text = it
            },
            trailingIcon = {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 10.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .clickable {
                                onSpeechClick()
                            }
                            .size(if (isListening) 24.dp * pulse else 24.dp),
                        painter = painterResource(R.drawable.speech_recog),
                        contentDescription = "speech",
                        colorFilter = ColorFilter.tint(color = if (isListening) Color.Red else LocalContentColor.current)
                    )
                    Spacer(modifier = Modifier.width(15.dp))

                    Image(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = textFieldValidator.text.isNotEmpty(),
                        ) {
                            onSendClick()
                        },
                        painter = painterResource(R.drawable.send_button),
                        contentDescription = "send",
                        alpha = if (textFieldValidator.text.isNotEmpty()) 1f else 0.5f
                    )

                }
            },
            placeholder = {
                Text(
                    modifier = Modifier,
                    text = textFieldValidator.placeholder,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = colorResource(R.color.color_4f3267),
                    lineHeight = 16.sp,
                    letterSpacing = 0.25.sp,
                )
            },
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                showKeyboardOnFocus = true,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedTextColor = colorResource(R.color.color_4f3267),
                unfocusedTextColor = colorResource(R.color.color_4f3267),
                disabledTextColor = colorResource(R.color.color_4f3267),
                errorTextColor = colorResource(R.color.color_4f3267),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedPlaceholderColor = colorResource(R.color.color_4f3267),
                unfocusedPlaceholderColor = colorResource(R.color.color_4f3267),
                disabledPlaceholderColor = colorResource(R.color.color_4f3267),
                errorPlaceholderColor = colorResource(R.color.color_4f3267),

                )


        )
    }
}



