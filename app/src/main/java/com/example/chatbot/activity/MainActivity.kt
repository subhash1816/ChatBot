package com.example.chatbot.activity

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatbot.ui.chat.ChatScreen
import com.example.chatbot.ui.login.LoginScreen
import com.example.chatbot.ui.navigation.NavRoutes
import com.example.chatbot.ui.register.RegisterScreen
import com.example.chatbot.ui.theme.ChatbotTheme
import com.example.chatbot.ui.utils.CustomLinearProgressBar
import com.example.chatbot.ui.utils.NoNetworkDialog
import com.example.chatbot.utils.Constants.Companion.NAV_WEBVIEW_URL
import com.example.chatbot.utils.Constants.Companion.REQUEST_CODE_SPEECH_INPUT
import com.example.chatbot.utils.CustomerInfo
import com.example.chatbot.utils.NetworkMonitor
import com.example.chatbot.utils.defaultEnterTransition
import com.example.chatbot.utils.defaultExitTransition
import com.example.chatbot.viewmodel.ChatViewModel
import com.example.chatbot.viewmodel.LoginViewModel
import com.example.chatbot.viewmodel.RegisterViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        initCustomerInfo()
        enableEdgeToEdge()
        setContent {
            ChatbotTheme {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }
    }

    private fun initCustomerInfo() {
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        if (isLoggedIn) FirebaseAuth.getInstance().signOut()
    }

    @Composable
    fun AppNavHost(navController: NavHostController) {
        val context = LocalContext.current
        val isOnline by NetworkMonitor.observe(context).collectAsStateWithLifecycle(initialValue = true)
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStack?.destination?.route
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        Log.d("Subhash", "AppNavHost: $isLoggedIn ")
        LaunchedEffect(isOnline) {
            if (!isOnline && currentRoute != NavRoutes.NO_NETWORK.route) {
                navController.navigate(NavRoutes.NO_NETWORK.route) {
                    popUpTo(NavRoutes.NO_NETWORK.route) { inclusive = true}
                }
            } else if (isOnline && currentRoute == NavRoutes.NO_NETWORK.route) {
                if (isLoggedIn) {
                    navController.navigate(NavRoutes.CHAT.route) {
                        popUpTo(NavRoutes.NO_NETWORK.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(NavRoutes.LOGIN.route) {
                        popUpTo(NavRoutes.NO_NETWORK.route) { inclusive = true }
                    }
                }
            }
        }

        NavHost(
            modifier = Modifier,
            navController = navController,
            startDestination =  NavRoutes.LOGIN.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() }
        ) {
            composable(route = NavRoutes.LOGIN.route,
                enterTransition = { defaultEnterTransition() },
                exitTransition = { defaultExitTransition() }
            ) {
                val loginViewModel: LoginViewModel by viewModels()
                 LoginScreen(loginViewModel, onLoginSuccess = {
                     navController.navigate(NavRoutes.CHAT.route)

                 }) {
                     navController.navigate(NavRoutes.REGISTER.route)
                   //  navController.navigate(NavRoutes.CHAT.route)
                 }
            }

            composable(route = NavRoutes.CHAT.route,
                enterTransition = { defaultEnterTransition() },
                exitTransition = { defaultExitTransition() }
            ) {
                val chatViewModel: ChatViewModel by viewModels()
                ChatScreen(chatViewModel, navController)
            }

            composable(route = NavRoutes.NO_NETWORK.route,
                enterTransition = { defaultEnterTransition() },
                exitTransition = { defaultExitTransition() }
            ) {
                NoNetworkDialog {
                    if (!isOnline) {
                        Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            composable(route = NavRoutes.REGISTER.route,
                enterTransition = { defaultEnterTransition() },
                exitTransition = { defaultExitTransition() }
            ) {
                val registerViewModel: RegisterViewModel by viewModels()
               RegisterScreen(registerViewModel, onRegisterSuccess = {
                   navController.navigate(NavRoutes.CHAT.route)
               }) {
                   navController.navigate(NavRoutes.LOGIN.route)
               }
            }

        }
    }


}

