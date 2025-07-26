package com.example.yoospace_android.data.repository

import com.example.yoospace_android.data.api.RetrofitInstance
import com.example.yoospace_android.data.model.CurrentUserResponse

class UserRepository {

    suspend fun getCurrentUser(): CurrentUserResponse {
        return RetrofitInstance.api.getCurrentUser()
    }
    suspend fun getUserById(userId: String): CurrentUserResponse {
        return RetrofitInstance.api.getUserProfile(userId)
    }
}