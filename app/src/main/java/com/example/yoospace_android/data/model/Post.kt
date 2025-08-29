package com.example.yoospace_android.data.model


data class Creator(
    val _id: String,
    val profile_image: String,
    val userName: String
)

data class Post(
    val _id: String,
    val content: String,
    val createdAt: String,
    val creator: Creator,
    val media: List<String>,
    var no_of_comment: Int,
    val no_of_like: Int,
    val isLiked: Boolean,
    val aspectRatio: PostAspectRatio?
)

data class PostAspectRatio(
    val x: Int = 1,
    val y: Int = 1
)