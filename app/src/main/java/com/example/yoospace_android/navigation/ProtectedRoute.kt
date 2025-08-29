package com.example.yoospace_android.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.repository.AuthRepository
import com.example.yoospace_android.utils.AppContext
import com.example.yoospace_android.utils.SocketManager

@Composable
fun ProtectedRoute(
    navController: NavController,
    content: @Composable () -> Unit
) {
    val tokenManager = remember { TokenManager }
    val token = tokenManager.getAccessToken()

    if (token == null) {
        // Redirect to login
        LaunchedEffect(Unit) {
            AuthRepository().logoutUser()
            navController.navigate(Routes.LOGIN) {
                popUpTo(0)
            }
        }
    } else {
        content()
    }
}
