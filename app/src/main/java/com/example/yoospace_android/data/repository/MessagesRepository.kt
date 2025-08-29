package com.example.yoospace_android.data.repository

import com.example.yoospace_android.data.api.RetrofitInstance
import com.example.yoospace_android.data.model.Conversation
import com.example.yoospace_android.data.model.ConversationItemModel
import com.example.yoospace_android.data.model.Group
import com.example.yoospace_android.data.model.GroupConversationData
import com.example.yoospace_android.data.model.GroupInvite
import com.example.yoospace_android.data.model.Message
import com.example.yoospace_android.data.model.Response
import com.example.yoospace_android.data.model.SendMessageRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepository @Inject constructor() {

//    to store group details
    private var _groupDetails = MutableStateFlow<GroupConversationData?>(null)
    val groupDetails : StateFlow<GroupConversationData?> = _groupDetails

    private var _allConversations = MutableStateFlow<List<ConversationItemModel>?>(null)
    val allConversations : StateFlow<List<ConversationItemModel>?> = _allConversations
    suspend fun getAllConversations(): Response<List<ConversationItemModel>> {
        val response = RetrofitInstance.api.getAllConversations()
        if (response.success) {
            _allConversations.value = response.data
        }
        return response
    }
    suspend fun getDirectConversationById(targetUserid: String): Response<Conversation> {
        return RetrofitInstance.api.getDirectConversationById(targetUserid)
    }
    suspend fun getGroupConversationById(conversationId: String): Response<GroupConversationData> {
        val response  = RetrofitInstance.api.getGroupConversationById(conversationId)
        if (response.success) {
            _groupDetails.value = response.data
        }
        return response
    }
    suspend fun getAllMessages(conversationId: String): Response<List<Message>> {
        return RetrofitInstance.api.getAllMessages(conversationId)
    }
    suspend fun sendMessage(conversationId: String, request: SendMessageRequest): Response<Message> {
        return RetrofitInstance.api.sendMessage(conversationId, request)
    }
    suspend fun setMessageRead(messageId: String) {
        return RetrofitInstance.api.setMessageRead(messageId)
    }
    suspend fun createGroupConversation(request: MultipartBody): Response<Group> {
        return RetrofitInstance.api.createGroupConversation(request)
    }
    suspend fun getAllGroupInvites(): Response<List<GroupInvite>> {
        return RetrofitInstance.api.getGroupInvites()
    }
    suspend fun acceptGroupInvite(conversationId: String): Response<Group> {
        return RetrofitInstance.api.acceptGroupInvite(conversationId)
    }
}