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
import com.example.chatbot.datalayer.repository.ChatRepo
import com.example.chatbot.ui.chat.ChatState
import com.example.chatbot.ui.login.LoginState
import com.example.chatbot.utils.Constants.Companion.QUERY_PLACEHOLDER
import com.example.chatbot.utils.CustomerInfo
import com.example.chatbot.utils.TextFieldValidator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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


    private val auth = FirebaseAuth.getInstance()

    private val _chatState = MutableStateFlow<ChatState>(ChatState.Idle)
    val chatState = _chatState.asStateFlow()


    var queryField = TextFieldValidator()
    private var speechRecognizer: SpeechRecognizer? = null
    var isListening: Boolean by mutableStateOf(false)

    private val cacheDb = CacheDataBase.getInstance(application)
    private val cacheRepo = CacheRepo(cacheDb.cacheDao())

    val messages: StateFlow<List<CacheEntity>> = cacheRepo.getFromCache(CustomerInfo.email)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        queryField.placeholder = QUERY_PLACEHOLDER
        initializeSpeechRecognizer()
    }

    fun onSendClick() {
        viewModelScope.launch {
            cacheRepo.saveToCache(
                msg = queryField.text,
                userId = CustomerInfo.email,
                isUser = true
            )
            queryField.text = ""
            fetchBotResp()
        }

    }

    private suspend fun fetchBotResp() {
        try {
            val response = withContext(Dispatchers.IO) { ChatRepo.fetchRemoteData() }
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    cacheRepo.saveToCache(
                        msg = it.reponseList?.random() ?: "Welcome to AI",
                        userId = CustomerInfo.email,
                        isUser = false
                    )
                }
            } else {
                _chatState.emit(ChatState.Failure(response?.message()))
                delay(1000)
                resetState()
            }
        } catch (e:Exception) {
            _chatState.emit(ChatState.Failure(e.localizedMessage?.toString()))
            delay(1000)
            resetState()
        }

    }

    private fun resetState() {
        _chatState.update { ChatState.Idle }
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
                        delay(1000)
                        resetState()
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