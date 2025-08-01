package com.example.yoospace_android.ui.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoospace_android.data.model.Comment
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.data.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel: ViewModel(){

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
            }
            catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorMessage = "Failed to fetch post: ${e.localizedMessage}"
            }
            finally {
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

    fun likePost(postId: String, function: () -> Unit={}) {
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
    fun unlikePost(postId: String, function: () -> Unit={}) {
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
}