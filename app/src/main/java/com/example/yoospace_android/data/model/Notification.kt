package com.example.yoospace_android.data.model

data class Notification(
    val _id: String,
    val toUserId: String,
    val type: String, // "group_invite", "reaction", "follow", "comment"
    val message: String,
    val userId: String? = null,
    val groupId: String? = null,
    val postId: String? = null,
    val image: String? = null,
    val createdAt: String,
    val updatedAt: String,
)
