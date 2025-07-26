package com.example.yoospace_android.data.api

import android.content.Context
import android.util.Log
import com.example.yoospace_android.data.local.TokenManager
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class TokenAuthenticator() : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val tokenManager = TokenManager
        val refreshToken = tokenManager.getRefreshToken() ?: return null
        Log.d("TokenAuthenticator", "Using refreshToken: $refreshToken")

        // Create a Retrofit instance WITHOUT authenticator to prevent loops
        val client = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://yoo-space.onrender.com") // Replace
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        val refreshResponse = api.refreshToken("Bearer $refreshToken").execute()
        Log.d("TokenAuthenticator", "Refresh called, response = $refreshResponse")


        return if (refreshResponse.isSuccessful && refreshResponse.body()?.data?.accessToken != null) {
            val newAccessToken = refreshResponse.body()!!.data.accessToken
            tokenManager.saveAccessToken(newAccessToken)

            // Retry original request with new access token
            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        } else {
            tokenManager.clearTokens()
            null // Logout user if refresh fails
        }
    }
}
