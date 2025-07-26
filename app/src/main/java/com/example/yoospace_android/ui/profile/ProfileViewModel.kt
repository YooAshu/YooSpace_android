package com.example.yoospace_android.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoospace_android.data.model.CurrentUser
import com.example.yoospace_android.data.model.CurrentUserResponse
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.data.repository.PostRepository
import com.example.yoospace_android.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel() : ViewModel() {
    var currentUser by mutableStateOf<CurrentUser?>(null)
        private set

    private val repository = UserRepository()
    var isLoading by mutableStateOf(false)
        private set
    var currentUserResponse by mutableStateOf<CurrentUserResponse?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchCurrentUser() {
        viewModelScope.launch {
            isLoading = true

            try {
                val response = repository.getCurrentUser()
                currentUserResponse = response
                currentUser = response.data
                errorMessage = null

            } catch (e: Exception) {
                errorMessage = "failed to get user : ${e.localizedMessage}"
                currentUserResponse = null
            } finally {
                isLoading = false
            }
        }

    }

    private val postRepository = PostRepository()
    var isPostsLoading by mutableStateOf(false)
        private set
    var postsList by mutableStateOf<List<Post>?>(null)
        private set
    var postsErrorMessage by mutableStateOf<String?>(null)
        private set


    fun fetchCurrentUserPosts() {
        viewModelScope.launch {
            isPostsLoading = true
            try {
                val response = postRepository.getCurrentUserPosts()
                postsList = response.data
                postsErrorMessage = null
            } catch (e: Exception) {
                postsErrorMessage = "failed to get user posts : ${e.localizedMessage}"
            } finally {
                isPostsLoading = false
            }
        }
    }

    var isLikedPostsLoading by mutableStateOf(false)
        private set
    var likedPostsList by mutableStateOf<List<Post>?>(null)
        private set
    var likedPostsErrorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchCurrentUserLikedPosts() {
        viewModelScope.launch {
            isLikedPostsLoading = true
            try {
                val response = postRepository.getCurrentUserLikedPosts()
                likedPostsList = response.data
                likedPostsErrorMessage = null
            } catch (e: Exception) {
                likedPostsErrorMessage = "failed to get user liked posts : ${e.localizedMessage}"
            } finally {
                isPostsLoading = false
            }
        }
    }

    var isUserByIdLoading by mutableStateOf(false)
        private set
    var userById by mutableStateOf<CurrentUser?>(null)
        private set
    var userByIdErrorMessage by mutableStateOf<String?>(null)

    fun fetchUserById(userId: String) {
        viewModelScope.launch {
            isUserByIdLoading = true
            try {
                val response = repository.getUserById(userId)
                userById = response.data
                userByIdErrorMessage = null
            } catch (e: Exception) {
                userByIdErrorMessage = "failed to get user by id : ${e.localizedMessage}"
                userById = null
            } finally {
                isUserByIdLoading = false
            }
        }
    }


}