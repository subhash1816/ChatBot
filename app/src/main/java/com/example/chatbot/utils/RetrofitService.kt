package com.example.chatbot.utils


import com.example.chatbot.datalayer.model.BotResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service interface.
 *
 * - [getUrlData] fetches data from remote server.
 */

interface RetrofitService {

    @GET("/v1/3e495f20-1b1c-4cc2-8eb0-0ba997ebd6c3")
    suspend fun getUrlData(): Response<BotResponseModel>?

}