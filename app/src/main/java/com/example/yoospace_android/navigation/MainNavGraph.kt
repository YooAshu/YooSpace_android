package com.example.yoospace_android.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.yoospace_android.data.model.ConversationUserParcel
import com.example.yoospace_android.data.model.GroupDetailsParcel
import com.example.yoospace_android.data.repository.MessagesRepository
import com.example.yoospace_android.data.repository.UserRepository
import com.example.yoospace_android.ui.common.BottomNav
import com.example.yoospace_android.ui.common.Header
import com.example.yoospace_android.ui.discoverPeople.DiscoverPeopleScreen
import com.example.yoospace_android.ui.feed.FeedScreen
import com.example.yoospace_android.ui.message.AllChatScreen
import com.example.yoospace_android.ui.message.ChatViewModelFactory
import com.example.yoospace_android.ui.message.DirectMessageScreen
import com.example.yoospace_android.ui.message.DirectMessageViewModel
import com.example.yoospace_android.ui.message.GroupChatViewModelFactory
import com.example.yoospace_android.ui.message.GroupDetailsScreen
import com.example.yoospace_android.ui.message.GroupMessageScreen
import com.example.yoospace_android.ui.message.GroupMessageViewModel
import com.example.yoospace_android.ui.post.AddPostScreen
import com.example.yoospace_android.ui.post.PostScreen
import com.example.yoospace_android.ui.profile.CurrentProfileScreen
import com.example.yoospace_android.ui.profile.UpdateProfileScreen
import com.example.yoospace_android.ui.profile.UserProfileScreen
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
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

@Composable
fun MainNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val hazeState = rememberHazeState()
//    user repo for sharing
    val userRepository = remember { UserRepository() }
//    message repo for sharing
    val messageRepository = remember { MessagesRepository() }
//    to get the current back stack entry and destination
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
//    to control the visibility of the header
    var headerVisible by remember { mutableStateOf(true) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = modifier.hazeSource(state = hazeState)) {
            Header(visible = headerVisible)
            NavHost(
                navController = navController,
                startDestination = Routes.PROFILE,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
            ) {
                composable(Routes.HOME) {
                    FeedScreen(
                        navController = navController,
                    ) { visible ->
                        headerVisible = visible
                    }
                }
                composable(Routes.PROFILE) {
                    CurrentProfileScreen(
                        navController = navController,
                        userRepository = userRepository
                    ) { visible ->
                        headerVisible = visible
                    }
                }
                composable(Routes.UPDATE_PROFILE) {
                    headerVisible = true
                    UpdateProfileScreen(navController = navController,
                        userRepository = userRepository)
                }
                composable(Routes.DISCOVER) {
                    headerVisible = true
                    DiscoverPeopleScreen(navController = navController)
                }
                composable(Routes.MESSAGES) {
                    headerVisible = true
                    val followersList = userRepository.currentUserFollowers
                    AllChatScreen(navController = navController, followersList = followersList)
                }
                composable(
                    route = Routes.DIRECT_CHAT,
                    arguments = listOf(navArgument("targetId") { type = NavType.StringType })
                ) { backStackEntry ->
                    headerVisible = false
                    val sender = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<ConversationUserParcel>("user")
                    val targetId = backStackEntry.arguments?.getString("targetId") ?: ""
                    // Create ViewModel for this chat
                    val directMessageViewModel: DirectMessageViewModel = viewModel(
                        factory = ChatViewModelFactory(targetUser = sender)
                    )

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
                    val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
                    // Create ViewModel for this chat
                    val groupMessageViewModel: GroupMessageViewModel = viewModel(
                        factory = GroupChatViewModelFactory(conversationId,messageRepository)
                    )
                    if (groupDetails != null) {
                        GroupMessageScreen(
                            viewModel = groupMessageViewModel, navController = navController,
                            groupDetails = groupDetails
                        )
                    }

                }
                composable(Routes.ADD_POST) {
                    headerVisible = true
                    AddPostScreen(navController = navController)
                }
                composable(
                    route = Routes.POST_DETAILS,
                    arguments = listOf(navArgument("postId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId") ?: ""
                    headerVisible = true
                    PostScreen(
                        navController = navController, postId = postId,
                    )
                }
                composable(
                    route = Routes.USER_PROFILE,
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    UserProfileScreen(
                        navController = navController,
                        userRepository = userRepository,
                        userId = userId,
                    ) {
                        headerVisible = it
                    }
                }
                composable(
                    route = Routes.GROUP_DETAILS
                ){backStackEntry ->
                    headerVisible = false
                    GroupDetailsScreen(
                        navController = navController,
                        groupDetails = messageRepository.groupDetails.collectAsState().value!!
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
                    .padding(bottom = 16.dp)
                    ,
                hazeState = hazeState
            )
        }
    }
}




