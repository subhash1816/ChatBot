package com.example.chatbot.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader



private fun getErrorRespStr(errorResponse: ResponseBody): String {
    val reader = BufferedReader(InputStreamReader(errorResponse.byteStream()))
    val out = StringBuilder()
    var line: String?
    while (reader.readLine().also { line = it } != null) {
        out.append(line)
    }
    reader.close()
    return out.toString()
}

private fun getErrMsg(errorRespStr: String): String? {
    var jsonObj = JSONObject()
    try {
        jsonObj = JSONObject(errorRespStr)
        val statusString = JSONObject(jsonObj["status"].toString())
        return statusString.getString("message")
    } catch (e: Exception) {

        try {
            return jsonObj.getString("message")
        } catch (exc: Exception) {

            try {
                return jsonObj.getString("error")
            } catch (e1: Exception) {

            }
        }
    }
    return null
}

fun AnimatedContentTransitionScope<*>.defaultEnterTransition(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))
}

fun AnimatedContentTransitionScope<*>.defaultExitTransition(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(300))
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}