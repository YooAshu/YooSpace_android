package com.example.yoospace_android.data.model

data class PostsResponse(
    val data: List<Post>,
    val message: String,
    val statusCode: Int,
    val success: Boolean
)

