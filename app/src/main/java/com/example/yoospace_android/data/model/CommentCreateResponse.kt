package com.example.yoospace_android.data.model

data class CommentCreateResponse(
    val data: Comment,
    val message: String,
    val statusCode: Int,
    val success: Boolean
)

