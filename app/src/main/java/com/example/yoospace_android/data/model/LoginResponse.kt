package com.example.yoospace_android.data.model


data class LoginResponse(
    val statusCode: Int,
    val data: LoginData,
    val message: String,
    val success: Boolean
)

data class LoginData(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)

data class User(
    val _id: String,
    val userName: String,
    val fullName: String,
    val email: String,
    val profile_image: String,
    val cover_image: String,
    val bio: String,
    val isAdmin: Boolean,
    val no_of_post: Int,
    val no_of_follower: Int,
    val no_of_following: Int,
    val createdAt: String,
    val updatedAt: String
)
