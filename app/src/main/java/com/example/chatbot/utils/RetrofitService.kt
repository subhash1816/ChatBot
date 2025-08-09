package com.example.chatbot.utils


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service interface.
 *
 * - [getUrlData] fetches data from remote server.
 */

interface RetrofitService {

    @GET("search/repositories")
    suspend fun getUrlData(
        @Query("q") query: String,
        @Query("sort") sort: String,
        @Query("order") order: String
    ): Response<String?>?

}