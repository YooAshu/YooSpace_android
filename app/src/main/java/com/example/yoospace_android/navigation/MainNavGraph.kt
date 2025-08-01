package com.example.yoospace_android.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.yoospace_android.ui.common.BottomNav
import com.example.yoospace_android.ui.common.Header
import com.example.yoospace_android.ui.feed.FeedScreen
import com.example.yoospace_android.ui.post.PostScreen
import com.example.yoospace_android.ui.profile.CurrentProfileScreen
import com.example.yoospace_android.ui.profile.UserProfileScreen

object Routes {
    //    authentication routes
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main app screens
    const val HOME = "home"
    const val PROFILE = "profile"
    const val MESSAGES = "messages"
    const val DISCOVER = "discover"

    // Dynamic routes with parameters
    const val POST_DETAILS = "post_details/{postId}"
    const val USER_PROFILE = "user_profile/{userId}"

    // Helper functions for navigation with parameters
    fun postDetails(postId: String) = "post_details/$postId"
    fun userProfile(userId: String) = "user_profile/$userId"
}

@Composable
fun MainNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = modifier) {
            Header()
            NavHost(
                navController = navController,
                startDestination = Routes.PROFILE,
            ) {
                composable(Routes.HOME) {
                    FeedScreen(navController = navController)
                }
                composable(Routes.PROFILE) {
                    CurrentProfileScreen(navController = navController)
                }
                composable(Routes.DISCOVER) {
//            SettingsScreen(navController = navController)
                }
                composable(
                    route = Routes.POST_DETAILS,
                    arguments = listOf(navArgument("postId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId") ?: ""
                    PostScreen(navController = navController, postId = postId)
                }
                composable(
                    route = Routes.USER_PROFILE,
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    UserProfileScreen(navController = navController, userId = userId)
                }
            }
        }
        BottomNav(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}


