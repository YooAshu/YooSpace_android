package com.example.yoospace_android.data.model

data class RefreshTokenResponse(
    val statusCode: Int,
    val data: TokenData,
    val message: String,
    val success: Boolean
)

data class TokenData(
    val accessToken: String
)
