package com.example.chatbot.datalayer.repository

import com.example.chatbot.utils.BaseNetworkSetup.createClient

object ChatRepo {
    suspend fun fetchRemoteData() = createClient().getUrlData()
}