package com.example.yoospace_android.data.model

data class FollowDetail(
    val _id: String,
    val fullName: String,
    var isFollowing: Boolean,
    val profile_image: String,
    val userName: String
)