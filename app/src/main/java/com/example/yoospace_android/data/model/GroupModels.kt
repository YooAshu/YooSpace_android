package com.example.yoospace_android.data.model

data class GroupInvite(
    val _id: String,
    val group: Group,
    val member: String,
    val invited_by: InvitedBy,
    val status: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)



data class InvitedBy(
    val _id: String,
    val userName: String,
    val profile_image: String
)

data class Group(
    val _id: String,
    val participants: List<String>,
    val isGroup: Boolean,
    val groupName: String,
    val avatar: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int,
    val lastMessage: String?
)