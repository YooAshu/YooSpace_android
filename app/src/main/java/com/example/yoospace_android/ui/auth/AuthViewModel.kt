package com.example.yoospace_android.ui.auth


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.LoginData
import com.example.yoospace_android.data.model.LoginRequest
import com.example.yoospace_android.data.model.RegisterRequest
import com.example.yoospace_android.data.model.RegisteredUser
import com.example.yoospace_android.data.model.Response
import com.example.yoospace_android.data.repository.AuthRepository
import com.example.yoospace_android.data.repository.UserRepository
import com.example.yoospace_android.utils.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
):ViewModel() {

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
    var loginState by mutableStateOf<LoginState>(LoginState.Idle)
        private set

    var registerState by mutableStateOf<RegisterState>(RegisterState.Idle)

    private val repository = AuthRepository()
    private val tokenManager = TokenManager

    // UI state variables
    var loginResponse: Response<LoginData>? by mutableStateOf(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var loginError by mutableStateOf<String?>(null)
        private set

    // Function to call login
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            loginState = LoginState.Loading
            try {
                val response = repository.loginUser(LoginRequest(email, password))
                loginResponse = response
                val user = response.data.user
                loginError = null
                tokenManager.saveTokens(
                    response.data.accessToken,
                    response.data.refreshToken,
                    userId = response.data.user._id
                )
                SocketManager.init("https://yoo-space.onrender.com", response.data.accessToken)
                tokenManager.saveUser(user)
                loginState = LoginState.Success
            } catch (e: retrofit2.HttpException) {
                // Extract error JSON body
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody ?: "").getString("message")
                } catch (_: Exception) {
                    "Login failed"
                }
                loginError = errorMessage
                loginResponse = null
                loginState = LoginState.Error(errorMessage)
            } catch (_: SocketTimeoutException) {
                loginError = "Request timed out"
                loginState = LoginState.Error("Request timed out")
            } catch (_: IOException) {
                loginError = "Network error"
                loginState = LoginState.Error("Network error")
            } catch (_: Exception) {
                loginError = "Unexpected error"
                loginState = LoginState.Error("Unexpected error")
            } finally {
                isLoading = false
            }
        }
    }

    var logoutError by mutableStateOf<String?>(null)
        private set

    fun logoutUser() {
        viewModelScope.launch {
            try {
                repository.logoutUser()
                tokenManager.clearTokens()
                SocketManager.disconnect()
                userRepository.clearNotifications()
            } catch (e: Exception) {
                logoutError = "Logout failed: ${e.localizedMessage}"
            }
        }
    }

    var registerError by mutableStateOf<String?>(null)
        private set

    var registerResponse: Response<RegisteredUser>? by mutableStateOf(null)
        private set

    fun registerUser(email: String, password: String, userName: String, fullName: String) {
        viewModelScope.launch {
            isLoading = true
            registerState = RegisterState.Loading
            try {
                val response = repository.registerUser(
                    RegisterRequest(
                        email = email,
                        password = password,
                        userName = userName,
                        fullName = fullName
                    )
                )
                registerResponse = response
                registerError = null
                registerState = RegisterState.Success
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody ?: "").getString("message")
                } catch (_: Exception) {
                    "Registration failed"
                }
                registerError = errorMessage
                registerState = RegisterState.Error(errorMessage)
                registerResponse = null
            } catch (_: IOException) {
                registerError = "Network error"
                registerState = RegisterState.Error("Network error")
                registerResponse = null
            } catch (_: Exception) {
                registerError = "Unexpected error"
                registerState = RegisterState.Error("Unexpected error")
                registerResponse = null
            } finally {
                isLoading = false
            }
        }
    }

}
