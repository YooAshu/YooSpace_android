package com.example.yoospace_android.data.repository

import android.util.Log
import androidx.compose.runtime.collectAsState
import com.example.yoospace_android.data.api.RetrofitInstance
import com.example.yoospace_android.data.model.CurrentUser
import com.example.yoospace_android.data.model.DiscoverUserData
import com.example.yoospace_android.data.model.FollowDetail
import com.example.yoospace_android.data.model.Notification
import com.example.yoospace_android.data.model.Response
import com.example.yoospace_android.data.model.SearchBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(){
//    current user followers list
    private val _currentUserFollowers = MutableStateFlow<List<FollowDetail>>( emptyList())
    val currentUserFollowers : StateFlow<List<FollowDetail>> = _currentUserFollowers


    suspend fun getCurrentUser(): Response<CurrentUser> {
        return RetrofitInstance.api.getCurrentUser()
    }
    suspend fun getUserById(userId: String): Response<CurrentUser> {
        return RetrofitInstance.api.getUserProfile(userId)
    }
    suspend fun getFollowers(): Response<List<FollowDetail>> {
        val response = RetrofitInstance.api.getFollowers()
        if (response.success) {
            _currentUserFollowers.value = response.data
        }
        return response
    }
    suspend fun getFollowing(): Response<List<FollowDetail>> {
        return RetrofitInstance.api.getFollowing()
    }
    suspend fun getUserFollowers(userId: String): Response<List<FollowDetail>> {
        return RetrofitInstance.api.getUserFollowers(userId)
    }
    suspend fun getUserFollowing(userId: String): Response<List<FollowDetail>> {
        return RetrofitInstance.api.getUserFollowing(userId)
    }
    suspend fun followUser(userId: String) {
        return RetrofitInstance.api.followUser(userId)
    }
    suspend fun unfollowUser(userId: String) {
        return RetrofitInstance.api.unfollowUser(userId)
    }
    suspend fun discoverUsers(): Response<DiscoverUserData> {
        return RetrofitInstance.api.discoverUsers()
    }
    suspend fun searchDiscoverUsers(query: SearchBody): Response<DiscoverUserData> {
        return RetrofitInstance.api.searchUsers(inputValue = query)
    }
    suspend fun updateProfile(update: MultipartBody.Builder): Response<CurrentUser> {
        return RetrofitInstance.api.updateProfile(update.build())
    }
    private val _notifications = MutableStateFlow<List<Notification>>( emptyList())
    val notifications : StateFlow<List<Notification>> = _notifications
    suspend fun getAllNotifications(forceRefresh: Boolean = false): List<Notification> {
        Log.d("UserRepository", "Fetching notifications, current size: ${notifications.value.size}")
        var response = notifications.value
        if (notifications.value.isEmpty() || forceRefresh){
            response = RetrofitInstance.api.getNotifications().data
            _notifications.value = response

        }
        return response
    }
    fun clearNotifications() {
        _notifications.value = emptyList()
        Log.d("not","${notifications.value}")
    }
}