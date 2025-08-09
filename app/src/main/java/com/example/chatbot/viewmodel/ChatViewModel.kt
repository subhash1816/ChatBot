package com.example.chatbot.viewmodel

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.datalayer.localroom.CacheDataBase
import com.example.chatbot.datalayer.localroom.CacheEntity
import com.example.chatbot.datalayer.localroom.CacheRepo
import com.example.chatbot.datalayer.model.MessageItem
import com.example.chatbot.ui.chat.ChatState
import com.example.chatbot.ui.login.LoginState
import com.example.chatbot.utils.Constants.Companion.NO_INTERNET
import com.example.chatbot.utils.CustomerInfo
import com.example.chatbot.utils.TextFieldValidator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * ViewModel that exposes the UI state and handles business logic.
 *
 * - Collects network status from NetworkMonitor.
 * - Calls repository to fetch data.
 * - Updates UI state with loading, success, or error.
 * - fetch data from remote once the 30 min time stamp expires.
 */

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableSharedFlow<ChatState>()
    val uiState = _uiState.asSharedFlow()
    private val _noNetwork = MutableSharedFlow<ChatState>()
    val noNetwork  = _noNetwork.asSharedFlow()
    var showProgress: Boolean by mutableStateOf(false)
    private val auth = FirebaseAuth.getInstance()


    private val _chatState = MutableStateFlow<ChatState>(ChatState.Idle)
    val chatState = _chatState.asStateFlow()


    var queryField = TextFieldValidator()
    private var speechRecognizer: SpeechRecognizer? = null
    var isListening: Boolean by mutableStateOf(false)

    private val botMessagesList = listOf(
        "Hello! How can I help you today?",
        "I'm here to assist you with that.",
        "Could you clarify what you mean?",
        "Interesting! Tell me more.",
        "I understand. Let’s work through it.",
        "That’s a great question!",
        "I’m not sure about that, but I can look it up.",
        "Here’s what I found on that topic.",
        "Got it! Let’s proceed.",
        "That might need more information to solve.",
        "Let me break that down for you.",
        "Can you provide an example?",
        "I can help you with the next steps.",
        "Thanks for sharing that.",
        "That’s worth exploring further.",
        "I see your point.",
        "Here’s a suggestion you might like.",
        "That’s a tricky one, but here’s my take.",
        "I think we can simplify this.",
        "Alright, let’s get started!"
    )

    private val cacheDb = CacheDataBase.getInstance(application)
    val cacheRepo = CacheRepo(cacheDb.cacheDao())

    val messages: StateFlow<List<CacheEntity>> = cacheRepo.getFromCache(CustomerInfo.email)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        queryField.placeholder = "Type your message here"
        initializeSpeechRecognizer()
    }

    fun onSendClick() {
        Log.d("Subhash", "onSendClick: ")
        viewModelScope.launch {
            cacheRepo.saveToCache(
                msg = queryField.text,
                userId = CustomerInfo.email,
                isUser = true
            )
            delay(500L)
            queryField.text = ""
            cacheRepo.saveToCache(
                msg = botMessagesList.random(),
                userId = CustomerInfo.email,
                isUser = false
            )
        }

    }

    fun signout() {
        auth.signOut()
        CustomerInfo.email = ""
    }

    private fun initializeSpeechRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    queryField.text = data?.firstOrNull().orEmpty()
                }
                override fun onError(error: Int) {
                    viewModelScope.launch {
                        _chatState.emit(ChatState.Failure(error.toString()))
                    }
                    isListening = false
                }
                override fun onReadyForSpeech(params: Bundle?) {
                    isListening = true
                }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    isListening = false
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        speechRecognizer?.startListening(intent)
    }

    fun startService() {
        if (isListening) {
            isListening = false
        } else {
            isListening = true
            startListening()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }


}