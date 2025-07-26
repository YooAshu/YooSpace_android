package com.example.yoospace_android.ui.feed

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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

    var feedPosts by mutableStateOf<List<Post>?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    fun fetchFeedPosts() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = postRepository.getFeedPosts()
                feedPosts = response.data
                errorMessage = null
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorMessage = "Failed to fetch feed posts: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
