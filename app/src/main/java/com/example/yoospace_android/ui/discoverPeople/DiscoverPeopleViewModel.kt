package com.example.yoospace_android.ui.discoverPeople

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoospace_android.data.model.DiscoverUser
import com.example.yoospace_android.data.model.SearchBody
import com.example.yoospace_android.data.repository.UserRepository
import kotlinx.coroutines.launch

class DiscoverPeopleViewModel  : ViewModel() {
    val repository = UserRepository()
    var isDiscoverPeopleLoading by mutableStateOf(false)
    private set
    var errorMessage by mutableStateOf<String?>(null)
    private set
    var discoverUsersList by mutableStateOf<List<DiscoverUser>>(emptyList())
    private set
    fun fetchDiscoverUsers() {
        viewModelScope.launch {
            isDiscoverPeopleLoading = true
            hasSearched = false
            try {
                val response = repository.discoverUsers()
                discoverUsersList = response.data.users
                errorMessage = null
            }
            catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isDiscoverPeopleLoading = false
            }
        }
    }
    var hasSearched by mutableStateOf(false)
        private set
    fun searchDiscoverUsers(query: String) {
        viewModelScope.launch {
            hasSearched = true
            isDiscoverPeopleLoading = true
            try {

                val response = repository.searchDiscoverUsers(SearchBody(inputValue = query))
                discoverUsersList = response.data.users
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isDiscoverPeopleLoading = false
            }
        }
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

}