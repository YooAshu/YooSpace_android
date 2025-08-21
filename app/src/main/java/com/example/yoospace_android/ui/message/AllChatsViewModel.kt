package com.example.yoospace_android.ui.message

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoospace_android.data.model.ConversationItemModel
import com.example.yoospace_android.data.model.Group
import com.example.yoospace_android.data.model.GroupInvite
import com.example.yoospace_android.data.repository.MessagesRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream

class AllChatsViewModel : ViewModel() {
    val repository = MessagesRepository()
    var convoList by mutableStateOf<List<ConversationItemModel>>(emptyList())
        private set
    var isLoading by mutableStateOf<Boolean>(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun getAllChats() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getAllConversations()
                convoList = response.data
                errorMessage = null
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                Log.d("AllChatsViewModel", "Error fetching conversations: ${e.localizedMessage}")
                errorMessage = e.message ?: "An error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun getRealPathFromUri(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        val fileName = "${System.currentTimeMillis()}.jpg"
        val tempFile = File(context.cacheDir, fileName)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return tempFile.absolutePath
    }

    var isCreatingGroup by mutableStateOf(false)
        private set

    fun createGroup(
        groupName: String,
        avatarUri: Uri?,
        selectedFollowers: List<String>,
        context: Context,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val requestBodyBuilder =
            MultipartBody.Builder().setType(MultipartBody.FORM)
        if (groupName.isNotEmpty() && selectedFollowers.isNotEmpty()) {
            requestBodyBuilder.addFormDataPart("groupName", groupName)
            val invitedToJson = JSONArray(selectedFollowers).toString()
            requestBodyBuilder.addFormDataPart("invitedTo", invitedToJson)
        } else {
            onError("Group name and participants cannot be empty")
            return
        }
        if (avatarUri != null) {
            val file = File(
                getRealPathFromUri(
                    context,
                    avatarUri
                )
            )
            val requestFile = file.asRequestBody("image/*".toMediaType())
            requestBodyBuilder.addFormDataPart(
                "avatar",
                "avatar_$groupName.jpg",
                requestFile
            )
        }
        viewModelScope.launch {
            try {
                isCreatingGroup = true
                val response = repository.createGroupConversation(requestBodyBuilder.build())
                val newGroup = response.data
                val newConvo = ConversationItemModel(
                    __v = newGroup.__v,
                    _id = newGroup._id,
                    avatar = newGroup.avatar,
                    groupName = newGroup.groupName,
                    lastMessage = null,
                    unseenCount = 0,
                    createdAt = newGroup.createdAt,
                    updatedAt = newGroup.updatedAt,
                    participants = emptyList(),
                    isGroup = true
                )
                convoList = listOf(newConvo) + convoList
                onSuccess()
            } catch (e: Exception) {
                Log.e("AllChatsViewModel", "Error creating group: ${e.localizedMessage}")
                onError(e.message ?: "An error occurred")
            } finally {
                isCreatingGroup = false
            }
        }
    }

    var groupInvitesList by mutableStateOf<List<GroupInvite>>(emptyList())
        private set
    fun getAllInvites(){
        viewModelScope.launch {
            try {
                val response = repository.getAllGroupInvites()
                groupInvitesList = response.data
            } catch (e: Exception) {
                Log.e("AllChatsViewModel", "Error fetching invites: ${e.localizedMessage}")
//                errorMessage = e.message ?: "An error occurred"
            }
        }
    }

    fun acceptInvite(conversationId: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = repository.acceptGroupInvite(conversationId)
                val acceptedInvite = response.data
                groupInvitesList = groupInvitesList.filter { it.group._id != acceptedInvite._id }
                convoList = convoList + ConversationItemModel(
                    __v = acceptedInvite.__v,
                    _id = acceptedInvite._id,
                    avatar = acceptedInvite.avatar,
                    groupName = acceptedInvite.groupName,
                    lastMessage = null,
                    unseenCount = 0,
                    createdAt = acceptedInvite.createdAt,
                    updatedAt = acceptedInvite.updatedAt,
                    participants = emptyList(),
                    isGroup = true
                )
                onSuccess()
            } catch (e: Exception) {
                Log.e("AllChatsViewModel", "Error accepting invite: ${e.localizedMessage}")
                onError(e.message ?: "An error occurred")
            }
        }
    }
}