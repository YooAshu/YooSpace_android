package com.example.yoospace_android.data.repository

import com.example.yoospace_android.data.api.RetrofitInstance
import com.example.yoospace_android.data.model.CurrentUserResponse
import com.example.yoospace_android.data.model.LoginRequest
import com.example.yoospace_android.data.model.LoginResponse
import com.example.yoospace_android.data.model.RegisterRequest
import com.example.yoospace_android.data.model.RegisterResponse


class AuthRepository {

    // This function actually calls the login endpoint via Retrofit
    suspend fun loginUser(request: LoginRequest): LoginResponse {
        return RetrofitInstance.api.loginUser(request)
    }

    suspend fun logoutUser() {
        return RetrofitInstance.api.logoutUser()
    }

    suspend fun registerUser(request: RegisterRequest): RegisterResponse {
        return RetrofitInstance.api.registerUser(request)
    }

}