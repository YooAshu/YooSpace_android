package com.example.yoospace_android.ui.message

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.GroupConversationData
import com.example.yoospace_android.data.model.Message
import com.example.yoospace_android.data.model.SendMessageRequest
import com.example.yoospace_android.data.repository.MessagesRepository
import com.example.yoospace_android.utils.SocketManager
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class GroupMessageViewModel @Inject constructor(
    private val repository: MessagesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val conversationId: String =
        savedStateHandle["conversationId"] ?: ""
    var groupDetails by mutableStateOf<GroupConversationData?>(null)
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    var messagesLoading by mutableStateOf(false)
        private set
    private val socket = SocketManager.getSocket()

    init {
        viewModelScope.launch {
            val convoId = getGroupDetails()
            joinConversation(convoId!!)
            getAllMessages(convoId)

            // Listen for incoming messages
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


    suspend fun getAllMessages(conversationId: String) {
        messagesLoading = true
        try {
            val response = repository.getAllMessages(conversationId)
            _messages.value = response.data
        } catch (e: Exception) {
            Log.e("DirectMessageViewModel", "Error fetching messages: ${e.message}")
        } finally {
            messagesLoading = false
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

    suspend fun getGroupDetails(): String? {
        try {
            val response = repository.getGroupConversationById(conversationId)
            groupDetails = response.data
            return groupDetails!!.conversation._id
        } catch (e: Exception) {
            Log.e("GroupMessageViewModel", "Error fetching group details: ${e.message}")
            return null
        }
    }

    var isSendingMessage by mutableStateOf(false)
        private set

    fun sendMessage(content: String) {
        viewModelScope.launch {
            isSendingMessage = true
            try {
                val conversationId = conversationId
                val message = SendMessageRequest(
                    text = content,
                    receiver = groupDetails!!.members.filter { it.member._id != TokenManager.getUserId() }
                        .map { it.member._id }
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

    override fun onCleared() {
        super.onCleared()
        socket?.emit("leave_conversation", conversationId)
        socket?.off("receive_message")
    }
}
