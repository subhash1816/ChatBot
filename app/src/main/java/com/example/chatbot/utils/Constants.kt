package com.example.chatbot.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Constants {
       companion object {

           const val URL_LOAD_SUCCESS = "Loaded the content successfullly"
           const val NO_NETWORK = "Please Check! No Internet Connection"
           const val RETRY = "RETRY"
           const val NAV_WEBVIEW_URL = "url"

           const val EMAIL_LABEL = "Enter Email"
           const val PASSWORD_LABEL = "Enter Password"
           const val CONFIRM_PASSWORD_LABEL = "Confirm Password"
           const val NAME_LABEL = "Enter Name"
           const val LOGIN_FAILED = "Login Failed"
           const val ENTER_VALID_EMAIL = "Please Enter Valid Email"
           const val ENTER_VALID_PASSWORD = "Please Enter Valid Password"
           const val ENTER_VALID_NAME = "Please Enter Valid Name"
           const val ENTER_CONFIRM_PASSWORD = "Please Enter Valid Name"
           const val PASSWORD_MISMATCH = "Password and Confirm Password should be same"
           const val REGISTRATION_FAILED = "Registration failed"
           const val QUERY_PLACEHOLDER = "Type your message here"
           const val PERMISSION_DENIED = "Permission Denied"
           const val SIGNOUT = "SignOut"
           const val LOGIN = "LOGIN"
           const val BUTTON_LOGIN = "LOGIN"
           const val NOT_HAVE_ACCOUNT = "Don't have an account? Register"
           const val ALREADY_HAVE_ACCOUNT = "Already have an account? Login"
           const val CHATBOT_NAME = "Chatbot"
           const val BUTTON_REGISTER = "REGISTER"
           const val REGISTER = "REGISTER"

       }
}
