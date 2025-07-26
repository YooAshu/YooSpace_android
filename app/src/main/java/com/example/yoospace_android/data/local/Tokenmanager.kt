package com.example.yoospace_android.data.local


import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.core.content.edit
import com.example.yoospace_android.utils.AppContext
import kotlinx.coroutines.flow.MutableStateFlow

object TokenManager {

    val isLoggedIn = MutableStateFlow(false)



    // 1️⃣ Create or retrieve a secure master key to encrypt/decrypt your SharedPreferences
    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    // 2️⃣ Create an instance of EncryptedSharedPreferences using:
    //    - A unique file name ("secure_prefs")
    //    - The generated master key
    //    - The app context
    //    - Key encryption scheme (AES256_SIV)
    //    - Value encryption scheme (AES256_GCM)
    private val prefs = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKey,
//        context,
        AppContext.get(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    init {
        // Automatically set login status on app start
        isLoggedIn.value = getAccessToken() != null
        Log.d("TokenManager", "Tokens ${prefs.getString("access_token", null)}")
    }
    // 3️⃣ Save both access and refresh tokens securely
    fun saveTokens(accessToken: String, refreshToken: String, userId: String) {
        prefs.edit().apply {
            putString("access_token", accessToken)  // Save access token
            putString("refresh_token", refreshToken) // Save refresh token
            putString("userId",userId)
            isLoggedIn.value = true
            apply() // Apply changes asynchronously
        }
    }
    fun saveAccessToken(accessToken: String) {
        prefs.edit { putString("access_token", accessToken) }
    }
    // 4️⃣ Retrieve access token when needed (e.g., for headers)
    fun getAccessToken(): String? = prefs.getString("access_token", null)

    // 5️⃣ Retrieve refresh token (used for refreshing expired access tokens)
    fun getRefreshToken(): String? = prefs.getString("refresh_token", null)
    fun getUserId(): String? = prefs.getString("userId", null)

    // 6️⃣ Clear all stored tokens (e.g., on logout)
    fun clearTokens() = prefs.edit {
        clear()
        isLoggedIn.value = false }
}
