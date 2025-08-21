package com.example.yoospace_android.data.repository

import com.example.yoospace_android.data.api.RetrofitInstance
import com.example.yoospace_android.data.model.LoginData
import com.example.yoospace_android.data.model.LoginRequest
import com.example.yoospace_android.data.model.RegisterRequest
import com.example.yoospace_android.data.model.RegisteredUser
import com.example.yoospace_android.data.model.Response


class AuthRepository {

    // This function actually calls the login endpoint via Retrofit
    suspend fun loginUser(request: LoginRequest): Response<LoginData> {
        return RetrofitInstance.api.loginUser(request)
    }

    suspend fun logoutUser() {
        return RetrofitInstance.api.logoutUser()
    }

    suspend fun registerUser(request: RegisterRequest): Response<RegisteredUser> {
        return RetrofitInstance.api.registerUser(request)
    }

}