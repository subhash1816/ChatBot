package com.example.chatbot.datalayer.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BotResponseModel(
    @SerializedName("reponse_list")
    val reponseList: List<String>?
): Parcelable
