package com.example.yoospace_android.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.yoospace_android.data.model.ConversationUserParcel
import com.example.yoospace_android.data.model.GroupDetailsParcel
import com.example.yoospace_android.ui.auth.AuthViewModel
import com.example.yoospace_android.ui.common.BottomNav
import com.example.yoospace_android.ui.common.Header
import com.example.yoospace_android.ui.discoverPeople.DiscoverPeopleScreen
import com.example.yoospace_android.ui.feed.FeedScreen
import com.example.yoospace_android.ui.feed.FeedViewModel
import com.example.yoospace_android.ui.message.AllChatScreen
import com.example.yoospace_android.ui.message.AllChatsViewModel
import com.example.yoospace_android.ui.message.DirectMessageScreen
import com.example.yoospace_android.ui.message.DirectMessageViewModel
import com.example.yoospace_android.ui.message.GroupDetailsScreen
import com.example.yoospace_android.ui.message.GroupMessageScreen
import com.example.yoospace_android.ui.message.GroupMessageViewModel
import com.example.yoospace_android.ui.notifications.NotificationScreen
import com.example.yoospace_android.ui.notifications.NotificationViewModel
import com.example.yoospace_android.ui.post.AddPostScreen
import com.example.yoospace_android.ui.post.PostScreen
import com.example.yoospace_android.ui.post.PostViewModel
import com.example.yoospace_android.ui.profile.CurrentProfileScreen
import com.example.yoospace_android.ui.profile.ProfileViewModel
import com.example.yoospace_android.ui.profile.UpdateProfileScreen
import com.example.yoospace_android.ui.profile.UserProfileScreen
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

object Routes {
    //    authentication routes
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main app screens
    const val HOME = "home"
    const val PROFILE = "profile"
    const val MESSAGES = "messages"
    const val DISCOVER = "discover"
    const val UPDATE_PROFILE = "update_profile"
    const val ADD_POST = "add_post"
    const val GROUP_DETAILS = "group_details"
    const val NOTIFICATIONS = "notifications"

    // Dynamic routes with parameters
    const val POST_DETAILS = "post_details/{postId}"
    const val USER_PROFILE = "user_profile/{userId}"
    const val DIRECT_CHAT = "direct_chat/{targetId}"
    const val GROUP_CHAT = "group_chat/{conversationId}"

    // Helper functions for navigation with parameters
    fun postDetails(postId: String) = "post_details/$postId"
    fun userProfile(userId: String) = "user_profile/$userId"
    fun directChat(targetId: String) = "direct_chat/$targetId"
    fun groupChat(conversationId: String) = "group_chat/$conversationId"
}

//@OptIn(ExperimentalSharedTransitionApi::class)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val hazeState = rememberHazeState()
//    to get the current back stack entry and destination
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
//    to control the visibility of the header
    var headerVisible by remember { mutableStateOf(true) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = modifier.hazeSource(state = hazeState)) {
            val authViewModel: AuthViewModel = hiltViewModel()
            Header(
                visible = headerVisible,
                onLogOutClick = { authViewModel.logoutUser() },
                onNotificationsClick = {
                    if (currentDestination != Routes.NOTIFICATIONS)
                        navController.navigate(Routes.NOTIFICATIONS)
                }
            )
            NavHost(
                navController = navController,
                startDestination = Routes.PROFILE,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
                route = "main_graph",
                modifier = Modifier.imePadding()
            ) {
                composable(Routes.HOME) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("main_graph")
                    }
                    val feedViewModel: FeedViewModel = hiltViewModel(parentEntry)
                    FeedScreen(
                        navController = navController,
                        viewModel = feedViewModel,
                    ) { visible ->
                        headerVisible = visible
                    }
                }
                composable(Routes.PROFILE) {
                    val viewModel: ProfileViewModel = hiltViewModel()
                    CurrentProfileScreen(
                        navController = navController,
                        viewModel = viewModel
                    ) { visible ->
                        headerVisible = visible
                    }
                }
                composable(Routes.UPDATE_PROFILE) {
                    headerVisible = true
                    val viewModel: ProfileViewModel = hiltViewModel()
                    UpdateProfileScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
                composable(Routes.DISCOVER) {
                    headerVisible = true
                    DiscoverPeopleScreen(navController = navController)
                }
                composable(Routes.MESSAGES) {
                    headerVisible = true
                    val viewModel: AllChatsViewModel = hiltViewModel()
                    AllChatScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
                composable(
                    route = Routes.DIRECT_CHAT,
                    arguments = listOf(navArgument("targetId") { type = NavType.StringType })
                ) { backStackEntry ->
                    headerVisible = false
                    val sender = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<ConversationUserParcel>("user")
                    // Create ViewModel for this chat
                    val directMessageViewModel: DirectMessageViewModel = hiltViewModel()
                    if (sender != null) {
                        DirectMessageScreen(
                            viewModel = directMessageViewModel, sender = sender,
                            navController = navController
                        )
                    }
                }
                composable(
                    route = Routes.GROUP_CHAT,
                    arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
                ) { backStackEntry ->
                    headerVisible = false
                    val groupDetails = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<GroupDetailsParcel>("groupDetails")

                    val groupMessageViewModel: GroupMessageViewModel = hiltViewModel()
                    if (groupDetails != null) {
                        GroupMessageScreen(
                            viewModel = groupMessageViewModel, navController = navController,
                            groupDetails = groupDetails
                        )
                    }

                }
                composable(Routes.ADD_POST) {
                    headerVisible = true
                    val viewModel: PostViewModel = hiltViewModel()
                    AddPostScreen(navController = navController, viewModel = viewModel)
                }
                composable(
                    route = Routes.POST_DETAILS,
                    arguments = listOf(navArgument("postId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId") ?: ""
                    val viewModel: PostViewModel = hiltViewModel()
                    headerVisible = true
                    PostScreen(
                        navController = navController, postId = postId, viewModel = viewModel
                    )
                }
                composable(
                    route = Routes.USER_PROFILE,
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    val viewModel: ProfileViewModel = hiltViewModel()
                    UserProfileScreen(
                        navController = navController,
                        viewModel = viewModel,
                        userId = userId,
                    ) {
                        headerVisible = it
                    }
                }
                composable(
                    route = Routes.GROUP_DETAILS
                ) { backStackEntry ->
                    headerVisible = false
                    GroupDetailsScreen(
                        navController = navController
                    )
                }

                composable(
                    route = Routes.NOTIFICATIONS
                )
                {
                    val notificationViewModel: NotificationViewModel = hiltViewModel()
                    headerVisible = true
                    NotificationScreen(
                        viewModel = notificationViewModel,
                        navController = navController
                    )
                }
            }

        }
        val excludeBottomNavRoutes = listOf(
            Routes.LOGIN,
            Routes.REGISTER,
            Routes.DIRECT_CHAT,
            Routes.GROUP_CHAT,
            Routes.GROUP_DETAILS
        )
        if (currentDestination !in excludeBottomNavRoutes) {
            BottomNav(
                navController = navController,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                hazeState = hazeState
            )
        }
    }
}




