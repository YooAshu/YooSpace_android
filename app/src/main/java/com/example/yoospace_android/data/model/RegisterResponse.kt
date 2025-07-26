package com.example.yoospace_android.data.model

data class RegisterResponse(
    val data: Data,
    val message: String,
    val statusCode: Int,
    val success: Boolean
)

data class Data(
    val __v: Int,
    val _id: String,
    val bio: String,
    val cover_image: String,
    val createdAt: String,
    val email: String,
    val fullName: String,
    val isAdmin: Boolean,
    val no_of_follower: Int,
    val no_of_following: Int,
    val no_of_post: Int,
    val profile_image: String,
    val updatedAt: String,
    val userName: String
)