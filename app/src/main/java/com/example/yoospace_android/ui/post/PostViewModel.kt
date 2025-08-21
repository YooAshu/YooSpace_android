package com.example.yoospace_android.ui.post

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoospace_android.data.model.Comment
import com.example.yoospace_android.data.model.CommentCreateRequest
import com.example.yoospace_android.data.model.Like
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.data.repository.PostRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import java.io.FileOutputStream

class PostViewModel : ViewModel() {

    var post by mutableStateOf<Post?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)

    val postRepository = PostRepository()

    fun getPostById(postId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = postRepository.getPostById(postId)
                post = response.data[0]
                errorMessage = null
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorMessage = "Failed to fetch post: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    var isCommentLoading by mutableStateOf(false)
        private set
    var commentErrorMessage by mutableStateOf<String?>(null)
    var comments by mutableStateOf<List<Comment>?>(null)
        private set

    fun getCommentsByPostId(postId: String) {
        viewModelScope.launch {
            isCommentLoading = true
            try {
                val response = postRepository.getCommentsByPostId(postId)
                comments = response.data
                errorMessage = null
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorMessage = "Failed to fetch comments: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    var errorPostLike by mutableStateOf<String?>(null)

    fun likePost(postId: String, function: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                postRepository.likePost(postId)
                errorPostLike = null
//                function()
                // Optionally, you can update the post state to reflect the new like count
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorPostLike = "Failed to like post: ${e.localizedMessage}"
            }
        }
    }

    fun unlikePost(postId: String, function: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                postRepository.unlikePost(postId)
                errorPostLike = null
//                function()
                // Optionally, you can update the post state to reflect the new like count
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorPostLike = "Failed to unlike post: ${e.localizedMessage}"
            }
        }
    }

    var errorCommentLike by mutableStateOf<String?>(null)
    fun likeComment(commentId: String) {
        viewModelScope.launch {
            try {
                postRepository.likeComment(commentId)
                errorCommentLike = null
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorCommentLike = "Failed to like comment: ${e.localizedMessage}"
            }
        }
    }

    fun unlikeComment(commentId: String) {
        viewModelScope.launch {
            try {
                postRepository.unlikeComment(commentId)
                errorCommentLike = null
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorCommentLike = "Failed to unlike comment: ${e.localizedMessage}"
            }
        }
    }

    var isCommenting by mutableStateOf(false)
        private set
    var commentPostErrorMessage by mutableStateOf<String?>(null)
    fun commentOnPost(postId: String, content: String) {
        viewModelScope.launch {
            isCommenting = true
            try {
                val response =
                    postRepository.commentOnPost(postId, CommentCreateRequest(content = content))
                comments = listOf(response.data) + (comments ?: emptyList())
                commentPostErrorMessage = null

            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                commentPostErrorMessage = "Failed to comment on post: ${e.localizedMessage}"
            } finally {
                isCommenting = false
            }
        }
    }

    var likesList by mutableStateOf<List<Like>>(emptyList())
        private set
    var likesListErrorMessage by mutableStateOf<String?>(null)
        private set
    var isLikesListLoading by mutableStateOf(false)
        private set

    fun fetchWhoLikedPost(postId: String) {
        viewModelScope.launch {
            isLikesListLoading = true
            try {
                val response = postRepository.getWhoLikedPost(postId)
                likesList = response.data
                Log.d("PostViewModel", "Likes List: $likesList")
                likesListErrorMessage = null
            } catch (e: Exception) {
                likesListErrorMessage = "Failed to fetch who liked post: ${e.localizedMessage}"
            } finally {
                isLikesListLoading = false
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


    var isCreatingPost by mutableStateOf(false)
        private set

    fun createPost(
        body: MultipartBody,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            isCreatingPost = true
            try {
                postRepository.createPost(body)
                onSuccess()
            } catch (e: Exception) {
                onError("Failed to create post: ${e.localizedMessage}")
            } finally {
                isCreatingPost = false
            }
        }
    }
}