package com.example.yoospace_android.data.model

data class DiscoverUserData(
    val users: List<DiscoverUser>
)

data class DiscoverUser(
    val _id: String,
    val fullName: String,
    val profile_image: String,
    val userName: String,
    val isFollowing: Boolean = false
)

data class SearchBody(
    val inputValue:String
)