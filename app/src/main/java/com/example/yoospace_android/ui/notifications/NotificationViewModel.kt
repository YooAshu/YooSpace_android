package com.example.yoospace_android.ui.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoospace_android.data.model.Notification
import com.example.yoospace_android.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: UserRepository, // inject or pass in constructor
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    private val _error = MutableStateFlow<String?>("")
    val error: StateFlow<String?> = _error

    init {
        getNotifications()
    }

    fun getNotifications(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) {
                _isRefreshing.value = true
            } else {
                _loading.value = true
            }
            _error.value = null
            try {
                Log.d("NotificationViewModel", "${repository.notifications.value.size}")
                val response = repository.getAllNotifications(forceRefresh)
                _notifications.value = response
            }
            catch (_: SocketTimeoutException){
                _error.value = "408"
            }
            catch (e: Exception) {
                _error.value = e.message
            } finally {
                if (forceRefresh) {
                    _isRefreshing.value = false
                } else {
                    _loading.value = false
                }
            }
        }
    }
}
