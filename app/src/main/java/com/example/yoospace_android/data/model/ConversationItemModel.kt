package com.example.yoospace_android.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Conversation(
    val _id: String,
    val participants: List<ConversationParticipants>,
    val isGroup: Boolean,
    val groupName: String,
    val avatar: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int,
    val lastMessage: String?
)


data class ConversationItemModel(
    val __v: Int,
    val _id: String,
    val avatar: String,
    val createdAt: String,
    val groupName: String,
    val isGroup: Boolean,
    val lastMessage: LastMessage?,
    val participants: List<ConversationParticipants>,
    val unseenCount: Int,
    val updatedAt: String
)

data class ConversationParticipants(
    val _id: String,
    val fullName: String,
    val profile_image: String,
    val userName: String
)


data class LastMessage(
    val __v: Int,
    val _id: String,
    val conversationId: String,
    val createdAt: String,
    val `receiver`: List<String>,
    val seenBy: List<String> = emptyList(),
    val sender: String,
    val text: String = "",
    val updatedAt: String
)

@Parcelize
data class ConversationUserParcel(
    val _id: String,
    val userName: String,
    val fullName: String,
    val profile_image: String
) : Parcelable


data class GroupConversationData(
    val conversation: Conversation,
    val members: List<Member>
)

data class Member(
    val _id: String,
    val group: String,
    val member: ConversationParticipants,
    val invited_by: String,
    val status: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)

@Parcelize
data class GroupDetailsParcel(
    val _id: String,
    val avatar: String,
    val groupName: String,
) : Parcelable

