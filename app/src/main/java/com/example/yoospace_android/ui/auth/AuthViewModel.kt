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
import kotlinx.coroutines.launch


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
//                tokenManager.saveUserInfo(
//                    userId = user._id,
//                    userName = user.userName,
//                    fullName = user.fullName,
//                    profile_image = user.profile_image,
//                    cover_image = user.cover_image,
//                    bio = user.bio,
//                    email = user.email
//                )
                tokenManager.saveUser(user)
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

    var registerResponse: Response<RegisteredUser>? by mutableStateOf(null)
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
