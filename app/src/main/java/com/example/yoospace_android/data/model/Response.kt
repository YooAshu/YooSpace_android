package com.example.yoospace_android.data.model

data class Response<T>(
    val statusCode: Int,
    val data: T,
    val message: String,
    val success: Boolean)
