package com.example.yoospace_android.ui.profile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun UserProfileScreen(
    viewModel: ProfileViewModel = viewModel(), navController: NavController,userId: String
) {

    LaunchedEffect(Unit) {
        viewModel.fetchUserById(userId = userId)
    }
    val user = viewModel.userById
    Text("User Profile: ${user?.userName ?: "Loading..."}")
}