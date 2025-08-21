package com.example.yoospace_android.ui.feed

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.data.repository.PostRepository
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    val postRepository: PostRepository = PostRepository()
    var isLoading by mutableStateOf(false)
    private set

    var feedPosts by mutableStateOf<List<Post>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)

        fun fetchFeedPosts() {
            viewModelScope.launch {
                isLoading = true
    //            feedPosts = emptyList()
                try {
                    val response = postRepository.getFeedPosts()
                    feedPosts = response.data
                    Log.d("FeedViewModel", "Fetched posts: ${feedPosts[0]}")
                    errorMessage = null
                } catch (e: Exception) {
                    // Handle the exception, e.g., log it or show a message to the user
                    errorMessage = "Failed to fetch feed posts: ${e.localizedMessage}"
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
                function()
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
                function()
                // Optionally, you can update the post state to reflect the new like count
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorPostLike = "Failed to unlike post: ${e.localizedMessage}"
            }
        }
    }
}
