package com.example.yoospace_android.data.model

data class CurrentUser(
    val _id: String,
    val bio: String,
    val cover_image: String,
    val createdAt: String,
    val email: String,
    val fullName: String,
    val isAdmin: Boolean,
    var no_of_follower: Int,
    var no_of_following: Int,
    val no_of_post: Int,
    val profile_image: String,
    val updatedAt: String,
    val userName: String,
    val isFollowing: Boolean = false
)