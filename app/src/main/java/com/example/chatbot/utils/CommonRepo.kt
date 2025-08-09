package com.example.chatbot.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object BaseNetworkSetup {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        })
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "GitProfileFinder/1.0")
                .build()
            chain.proceed(request)
        }
        .retryOnConnectionFailure(true)        // Auto-retry failed requests
        .build()

    private var retrofitUrl: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
         .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun createClient() : RetrofitService = retrofitUrl.create(RetrofitService::class.java)



}