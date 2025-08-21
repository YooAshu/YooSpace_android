package com.example.yoospace_android.data.model

data class Like(
    val _id: String,
    val usersLiked: UsersLiked
)

data class UsersLiked(
    val _id: String,
    val profile_image: String,
    val userName: String
)