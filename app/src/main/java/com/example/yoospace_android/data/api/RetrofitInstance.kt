package com.example.yoospace_android.data.api

// Retrofit: Main networking library for HTTP requests
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.utils.AppContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// OkHttp: Used to customize request behavior (like logging, headers)
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

// Singleton object: only one Retrofit instance will be created and shared across the app
object RetrofitInstance {

    // Base URL of your backend API (make sure to include trailing slash if needed)
    // Example: https://api.yoospace.com/
    private const val BASE_URL = "https://yoo-space.onrender.com"

    // Logging Interceptor: useful for debugging API request/response logs in Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Logs full request and response bodies
    }
    private val client = OkHttpClient.Builder()
        .authenticator(TokenAuthenticator())
        .addInterceptor(AuthInterceptor(TokenManager))
        .addInterceptor(loggingInterceptor)
        .build()


    // Lazy initialization of Retrofit — only created once when first accessed
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Set the root URL for all requests
            .client(client) // Attach OkHttpClient with interceptors
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson to parse JSON responses
            .build()
            .create(ApiService::class.java) // Generate implementation of ApiService interface
    }
}
