package com.example.litterboom.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import android.util.Log
import com.example.litterboom.MyApplication
import com.example.litterboom.data.SessionManager

object ApiClient {
    private const val BASE_URL = "https://litterboomapi.onrender.com"  // Change to your API base URL
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    val apiService: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val token = SessionManager.getToken(MyApplication.instance)
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
                Log.d("API", "Sending token: ${token.take(50)}...")
            } else {
                Log.w("API", "No token! URL: ${chain.request().url}")
            }
            chain.proceed(requestBuilder.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
