package com.example.yoospace_android.data.model

data class Message(
    val __v: Int,
    val _id: String,
    val conversationId: String,
    val createdAt: String,
    val receiver: List<MessageParticipant>,
    val seenBy: List<String>,
    val sender: MessageParticipant,
    val text: String,
    val updatedAt: String
)

data class MessageParticipant(
    val _id: String,
    val profile_image: String,
    val userName: String
)

data class SendMessageRequest(
    val text: String,
    val receiver: List<String>
)
