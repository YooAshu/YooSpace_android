package com.example.yoospace_android.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.yoospace_android.ui.auth.AuthViewModel
import com.example.yoospace_android.ui.auth.LoginScreen
import com.example.yoospace_android.ui.auth.RegisterScreen
import com.example.yoospace_android.ui.feed.FeedViewModel


@Composable
fun AuthNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Routes.REGISTER) {
            val viewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
