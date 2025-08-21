package com.example.yoospace_android.ui.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.CurrentUser
import com.example.yoospace_android.data.model.FollowDetail
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.data.repository.PostRepository
import com.example.yoospace_android.data.repository.UserRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    var currentUser by mutableStateOf<CurrentUser?>(null)
        private set

    //    private val repository = UserRepository()
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchCurrentUser() {
        viewModelScope.launch {
            isLoading = true

            try {
                val response = repository.getCurrentUser()
                currentUser = response.data
                errorMessage = null

            } catch (e: Exception) {
                errorMessage = "failed to get user : ${e.localizedMessage}"
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
                Log.e("ProfileViewModel", "Error fetching user posts: ${e}")
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

    var isUsersPostsLoading by mutableStateOf(false)
        private set
    var usersPostsList by mutableStateOf<List<Post>?>(null)
        private set
    var userPostsErrorMessage by mutableStateOf<String?>(null)
        private set


    fun fetchUsersPostsById(userId: String) {
        viewModelScope.launch {
            isUsersPostsLoading = true
            try {
                val response = postRepository.getPostsByUserId(userId)
                usersPostsList = response.data
                userPostsErrorMessage = null
            } catch (e: Exception) {
                userPostsErrorMessage = "failed to get user posts : ${e.localizedMessage}"
            } finally {
                isUsersPostsLoading = false
            }
        }
    }

    var errorPostLike by mutableStateOf<String?>(null)

    fun likePost(postId: String) {
        viewModelScope.launch {
            try {
                postRepository.likePost(postId)
                errorPostLike = null
                // Optionally, you can update the post state to reflect the new like count
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorPostLike = "Failed to like post: ${e.localizedMessage}"
            }
        }
    }

    fun unlikePost(postId: String) {
        viewModelScope.launch {
            try {
                postRepository.unlikePost(postId)
                errorPostLike = null
                // Optionally, you can update the post state to reflect the new like count
            } catch (e: Exception) {
                // Handle the exception, e.g., log it or show a message to the user
                errorPostLike = "Failed to unlike post: ${e.localizedMessage}"
            }
        }
    }

    var isFollowListLoading by mutableStateOf(false)
        private set
    var followersList by mutableStateOf<List<FollowDetail>?>(emptyList())
        private set
    var followListErrorMessage by mutableStateOf<String?>(null)
        private set

    fun getCurrentUserFollowers() {
        viewModelScope.launch {
            isFollowListLoading = true
            try {
                val response = repository.getFollowers()
                followersList = response.data
                followListErrorMessage = null
            } catch (e: Exception) {
                followListErrorMessage = "failed to get user followers : ${e.localizedMessage}"
            } finally {
                isFollowListLoading = false
            }
        }
    }

    var followingList by mutableStateOf<List<FollowDetail>?>(emptyList())
        private set

    fun getCurrentUserFollowing() {
        viewModelScope.launch {
            isFollowListLoading = true
            try {
                val response = repository.getFollowing()
                followingList = response.data
                followListErrorMessage = null
            } catch (e: Exception) {
                followListErrorMessage = "failed to get user following : ${e.localizedMessage}"
            } finally {
                isFollowListLoading = false
            }
        }
    }

    // In ProfileViewModel
    fun addUserToCurrentFollowingList(userId: String) {
        if (followingList?.any { it._id == userId } == true) return
        followersList?.find { it._id == userId }?.let { user ->
            followingList = (followingList ?: emptyList()) + user
        }
    }

    var userFollowersList by mutableStateOf<List<FollowDetail>?>(emptyList())

    fun getUserFollowers(userId: String) {
        viewModelScope.launch {
            isFollowListLoading = true
            try {
                val response = repository.getUserFollowers(userId)
                userFollowersList = response.data
                followListErrorMessage = null
            } catch (e: Exception) {
                followListErrorMessage = "failed to get user followers : ${e.localizedMessage}"
            } finally {
                isFollowListLoading = false
            }
        }
    }

    var userFollowingList by mutableStateOf<List<FollowDetail>?>(emptyList())
        private set

    fun getUserFollowing(userId: String) {
        viewModelScope.launch {
            isFollowListLoading = true
            try {
                val response = repository.getUserFollowing(userId)
                userFollowingList = response.data
                followListErrorMessage = null
            } catch (e: Exception) {
                followListErrorMessage = "failed to get user following : ${e.localizedMessage}"
            } finally {
                isFollowListLoading = false
            }
        }
    }

    fun addCurrentUserToUserFollowerList() {
        if (userFollowersList?.any { it._id == TokenManager.getUserId() } == true) return

        userFollowersList = (userFollowersList ?: emptyList()) + FollowDetail(
            _id = TokenManager.getUserId() ?: "",
            userName = TokenManager.getUser()?.userName ?: "",
            fullName = TokenManager.getUser()?.fullName ?: "",
            profile_image = TokenManager.getUser()?.profile_image ?: "",
            isFollowing = true
        )

    }

    var followLoading by mutableStateOf(false)
        private set
    var followErrorMessage by mutableStateOf<String?>(null)
        private set

    fun followUser(userId: String) {
        viewModelScope.launch {
            followLoading = true
            try {
                repository.followUser(userId)
                followErrorMessage = null
            } catch (e: Exception) {
                followErrorMessage = "failed to follow user : ${e.localizedMessage}"
            } finally {
                followLoading = false
            }
        }
    }

    fun unfollowUser(userId: String) {
        viewModelScope.launch {
            followLoading = true
            try {
                repository.unfollowUser(userId)
                followErrorMessage = null
            } catch (e: Exception) {
                followErrorMessage = "failed to unfollow user : ${e.localizedMessage}"
            } finally {
                followLoading = false
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

    var isUpdatingProfile by mutableStateOf(false)
    var toastMessage by mutableStateOf<String?>(null)
    fun updateProfile(update: MultipartBody.Builder) {
        viewModelScope.launch {
            isUpdatingProfile = true
            toastMessage = "Updating profile..."
            try {
                val response = repository.updateProfile(update)

//                TokenManager.saveUserInfo(
//                    response.data._id,
//                    response.data.userName,
//                    response.data.fullName,
//                    response.data.profile_image,
//                    response.data.cover_image,
//                    response.data.bio,
//                    response.data.email,
//                )
                TokenManager.saveUser(response.data)
                toastMessage = "Profile updated successfully"
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Update failed $e")
                toastMessage = "Failed to update profile"
            } finally {
                isUpdatingProfile = false
            }
        }
    }

}