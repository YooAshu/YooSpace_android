package com.example.yoospace_android.ui.auth


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.LoginRequest
import com.example.yoospace_android.data.model.LoginResponse
import com.example.yoospace_android.data.model.RegisterRequest
import com.example.yoospace_android.data.model.RegisterResponse
import com.example.yoospace_android.data.repository.AuthRepository


class AuthViewModel: ViewModel() {

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
    var loginResponse by mutableStateOf<LoginResponse?>(null)
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
                loginError = null
                tokenManager.saveTokens(
                    response.data.accessToken,
                    response.data.refreshToken,
                    userId = response.data.user._id
                )
                loginState = LoginState.Success
            } catch (e: Exception) {
                loginError = "Login failed: ${e.localizedMessage}"
                loginResponse = null
                loginState = LoginState.Error("Login failed")
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
            } catch (e: Exception) {
                logoutError = "Logout failed: ${e.localizedMessage}"
            }
        }
    }

    var registerError by mutableStateOf<String?>(null)
        private set

    var registerResponse by mutableStateOf<RegisterResponse?>(null)
        private set

    fun registerUser(email: String, password: String,userName:String,fullName:String){
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
            }
            catch (e: Exception) {
                registerState = RegisterState.Error("Registration failed: ${e.localizedMessage}")
                registerError = "Registration failed: ${e.localizedMessage}"
                registerResponse = null

            }
            finally {
                isLoading = false
            }

        }
    }
}
