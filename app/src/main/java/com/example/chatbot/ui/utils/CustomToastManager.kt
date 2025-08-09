package com.example.chatbot.ui.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

object CustomToastManager {
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        val toast = Toast(context)
        toast.duration = duration

        // Create a ComposeView to host your custom Compose UI
        val composeView = ComposeView(context).apply {
            setContent {
                // Your custom Composable for the toast UI
                CustomToast(message = message)
            }
        }
        toast.view = composeView
        toast.show()
    }
}