package com.example.yoospace_android.ui.profile

import android.R
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.ConversationUserParcel
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.data.repository.UserRepository
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.Post
import com.example.yoospace_android.ui.profile.components.UserInfo
import com.example.yoospace_android.ui.theme.LocalExtraColors

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun UserProfileScreen(
    userRepository: UserRepository,
    navController: NavController,
    userId: String,
    onScroll: (Boolean) -> Unit
) {
    val viewModel = remember { ProfileViewModel(userRepository) }

    ProtectedRoute(navController = navController) {
        val listState = rememberLazyListState()
        var previousIndex by remember { mutableIntStateOf(0) }
        var previousOffset by remember { mutableIntStateOf(0) }
        val threshold = 30 // px threshold before toggling

        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
                .collect { (index, offset) ->

                    val delta = offset - previousOffset

                    val scrollingUp = index < previousIndex ||
                            (index == previousIndex && delta < -threshold) || // only if moved up more than threshold
                            offset == 0 // always show when scrolled to top

                    val scrollingDown = index > previousIndex ||
                            (index == previousIndex && delta > threshold)

                    when {
                        scrollingUp -> onScroll(true)  // show header
                        scrollingDown -> onScroll(false) // hide header
                        else -> Unit // ignore small scroll changes within threshold
                    }

                    previousIndex = index
                    previousOffset = offset
                }
        }


        val posts = remember { mutableStateOf<List<Post>?>(null) }
        LaunchedEffect(Unit) {
            viewModel.fetchUserById(userId = userId)
            viewModel.fetchUsersPostsById(userId = userId)
            viewModel.getUserFollowers(userId = userId)
            viewModel.getUserFollowing(userId = userId)
        }
        val user = viewModel.userById
        val isPostsLoading = viewModel.isPostsLoading
        val postsError = viewModel.postsErrorMessage
        var followersList = viewModel.userFollowersList
        val followingList = viewModel.userFollowingList
        posts.value = viewModel.usersPostsList
        val blurChange = remember { mutableStateOf(false) }
        val animatedBlur by animateDpAsState(
            targetValue = if (blurChange.value) 5.dp else 0.dp,
            label = "blurTransition"
        )

        if (user != null) {
            val isFollowing = remember { mutableStateOf(user.isFollowing) }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .blur(animatedBlur)
                    .fillMaxHeight()
                    .background(LocalExtraColors.current.listBg)
            ) {
                item {
                    UserInfo(
                        user,
                        navController,
                        followersList = followersList!!,
                        followingList = followingList!!,
                        onFollowClick = { userId, isFollowing ->
                            if (isFollowing) {
                                viewModel.unfollowUser(userId)
                            } else {
                                viewModel.followUser(userId)
                            }
                            viewModel.userFollowersList?.forEach { it ->
                                if (it._id == userId) {
                                    it.isFollowing = !isFollowing
                                }
                            }
                            viewModel.userFollowingList?.forEach { it ->
                                if (it._id == userId) {
                                    it.isFollowing = !isFollowing
                                }
                            }
                        },
                        onBlurBgChange = {blur->
                            blurChange.value = blur
                        }
                    )
                }
                item{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(bottom = 30.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            if(isFollowing.value) {
                                viewModel.unfollowUser(user._id)
                                viewModel.userFollowersList = followersList?.filter { it._id != TokenManager.getUserId() }
                                TokenManager.updateUserFollowingNo(-1)
                                viewModel.userById?.no_of_follower -=1
                            }
                            else {
                                viewModel.followUser(user._id)
                                TokenManager.updateUserFollowingNo(1)
                                viewModel.addCurrentUserToUserFollowerList()
                                viewModel.userById?.no_of_follower +=1
                            }

                            isFollowing.value = !isFollowing.value
                        },
                            colors= ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                        ) {
                            Text(
                                text = if (isFollowing.value) "Following" else "Follow",
                                color = LocalExtraColors.current.textPrimary,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .width(120.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 20.dp, vertical = 5.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(onClick = {
                            val user = ConversationUserParcel(
                                _id = user._id,
                                fullName = user.fullName,
                                profile_image = user.profile_image,
                                userName = user.userName
                            )
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("user", user)
                            navController.navigate(Routes.directChat(user._id))
                        },
                            colors= ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                        ) {
                            Text(
                                text = "Message",
                                color = LocalExtraColors.current.textPrimary,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .width(120.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                item {
                    if (isPostsLoading) {
                        Text(
                            "Loading posts...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else if (postsError != null) {
                        Text("Error loading posts: $postsError")
                    } else if (posts.value?.isEmpty() == true) {
                        Text("No posts available.")
                    }
                }
                if (posts.value?.isNotEmpty() == true) {

                    items(
                        posts.value!!,
                        key = { post -> post._id + "-" + post.no_of_like + "-" + post.isLiked }) { post ->
                        Post(
                            post,
                            modifier = Modifier.padding(5.dp),
                            onPostClick = { postId ->
                                navController.navigate(Routes.postDetails(postId))
                            },
                            onProfileClick = { userId ->
                                if (user._id == userId) {
//                                        navController.navigate("profile")
                                } else
                                    navController.navigate(Routes.userProfile(userId))
                            },
                            onLikeClick = { postId, isLiked ->
                                if (isLiked) {
                                    viewModel.unlikePost(postId)
                                } else {
                                    viewModel.likePost(postId)
                                }
                            },
                        )
                    }
                }
                item {
                    Spacer(
                        modifier = Modifier
                            .height(80.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }

}