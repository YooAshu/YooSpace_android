package com.example.yoospace_android.ui.message

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yoospace_android.data.model.Conversation
import com.example.yoospace_android.data.model.ConversationParticipants
import com.example.yoospace_android.data.model.ConversationUserParcel
import com.example.yoospace_android.data.model.Message
import com.example.yoospace_android.data.model.SendMessageRequest
import com.example.yoospace_android.data.repository.MessagesRepository
import com.example.yoospace_android.utils.SocketManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class DirectMessageViewModel(
    private val targetUser: ConversationUserParcel
) : ViewModel() {

    val repository = MessagesRepository()
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages
    private val socket = SocketManager.getSocket()
    var messagesLoading = mutableStateOf(false)
        private set
    var globalScopeConversationId: String = ""

    init {
        viewModelScope.launch {
            val conversationId = getConversationId()
            globalScopeConversationId = conversationId
            getAllMessages(conversationId)
            joinConversation(conversationId)
            socket?.on("receive_message") { args ->

                if (args.isNotEmpty()) {
                    val data = args[0] as JSONObject
                    Log.d("ChatViewModel", "Received args: $data")
                    val gson = Gson()
                    val message: Message = gson.fromJson(data.toString(), Message::class.java)
                    setReadStatus(message._id)
                    viewModelScope.launch {
                        _messages.value = listOf(message) + _messages.value
                        Log.d("ChatViewModel", "Received message: $message")
                    }
                }
            }
        }

    }

    private fun joinConversation(convoId: String) {
        socket?.emit("join_conversation", convoId)
    }

    suspend fun getConversationId(): String {
        try {
            val response = repository.getDirectConversationById(targetUser._id)
            return response.data._id
        } catch (e: Exception) {
            Log.e("DirectMessageViewModel", "Error fetching conversation: ${e.message}")
            return ""
        }

    }


    suspend fun getAllMessages(conversationId: String) {
        messagesLoading.value = true
        try {
            val response = repository.getAllMessages(conversationId)
            _messages.value = response.data
        } catch (e: Exception) {
            Log.e("DirectMessageViewModel", "Error fetching messages: ${e.message}")
        } finally {
            messagesLoading.value = false
        }

    }

    var isSendingMessage by mutableStateOf(false)
        private set

    fun sendMessage(content: String) {
        viewModelScope.launch {
            isSendingMessage = true
            try {
                val conversationId = globalScopeConversationId
                val message = SendMessageRequest(
                    text = content,
                    receiver = listOf(targetUser._id)
                )
                repository.sendMessage(conversationId, message)
//                _messages.value = listOf(response.data) + _messages.value
            } catch (e: Exception) {
                Log.e("DirectMessageViewModel", "Error sending message: ${e.message}")
            } finally {
                isSendingMessage = false
            }
        }
    }

    fun setReadStatus(messageId: String) {
        viewModelScope.launch {
            try {
                repository.setMessageRead(messageId)
            } catch (e: Exception) {
                Log.e("DirectMessageViewModel", "Error setting read status: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (globalScopeConversationId.isNotEmpty())
            socket?.emit("leave_conversation", globalScopeConversationId)
        socket?.off("receive_message")
    }
}

class ChatViewModelFactory(
    private val targetUser: ConversationUserParcel? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DirectMessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DirectMessageViewModel(targetUser = targetUser!!) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
