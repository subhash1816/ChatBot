package com.example.chatbot.ui.navigation

import android.net.Uri
import com.example.chatbot.utils.Constants.Companion.NAV_WEBVIEW_URL

sealed class NavRoutes(val route: String) {
    data object HOME : NavRoutes("home")
    data object LOGIN : NavRoutes("login")
    data object REGISTER : NavRoutes("register")
    data object CHAT : NavRoutes("chat")
    data object NO_NETWORK : NavRoutes("noNetwork")
    data object WEBVIEW : NavRoutes("webview/{${NAV_WEBVIEW_URL}}") {
        fun createRoute(url: String) = "webview/${Uri.encode(url)}"
    }
}