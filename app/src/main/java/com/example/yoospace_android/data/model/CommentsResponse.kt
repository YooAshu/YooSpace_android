package com.example.yoospace_android.data.model

data class Comment(
    val _id: String,
    val commented_by: String,
    val content: String,
    val createdAt: String,
    val no_of_like: Int,
    val user: Creator,
    val isLiked: Boolean
)