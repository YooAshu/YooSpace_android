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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _feedPosts = MutableStateFlow<List<Post>>(emptyList())
    val feedPosts: StateFlow<List<Post>> = _feedPosts
    var isLoading by mutableStateOf(true)
        private set

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    var errorMessage by mutableStateOf<String?>("")
    fun loadFeedPostsIfEmpty() {
        if (_feedPosts.value.isEmpty()) {
            fetchFeedPosts()
        }
    }

    fun refreshFeedPosts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            errorMessage = null
            try {
                _feedPosts.value = postRepository.getFeedPosts(forceRefresh = true)
            } catch (
                e: SocketTimeoutException
            ) {
                errorMessage = "408"
            } catch (e: Exception) {
                errorMessage = "Failed to fetch feed posts: ${e.localizedMessage}"
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun fetchFeedPosts() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                _feedPosts.value = postRepository.getFeedPosts(forceRefresh = true)

            } catch (
                e: SocketTimeoutException
            ) {
                errorMessage = "408"
            } catch (e: Exception) {
                errorMessage = "Failed to fetch feed posts: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    var errorPostLike by mutableStateOf<String?>(null)
    var likingPosts by mutableStateOf(setOf<String>())
        private set

    fun likePost(postId: String, function: () -> Unit = {}) {
        viewModelScope.launch {
            likingPosts = likingPosts + postId
            try {
                postRepository.likePost(postId)
                errorPostLike = null
                function()
                // Optionally, you can update the post state to reflect the new like count
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorPostLike = "Failed to like post: ${e.localizedMessage}"
            } finally {
                likingPosts = likingPosts - postId  // remove it once done
            }
        }
    }

    fun unlikePost(postId: String, function: () -> Unit = {}) {
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
