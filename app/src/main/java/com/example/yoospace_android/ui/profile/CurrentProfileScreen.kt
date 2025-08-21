package com.example.yoospace_android.ui.profile

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.data.repository.UserRepository
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.Post
import com.example.yoospace_android.ui.profile.components.PostTypeButtons
import com.example.yoospace_android.ui.profile.components.UserInfo
import com.example.yoospace_android.ui.theme.LocalExtraColors

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CurrentProfileScreen(
//    viewModel: ProfileViewModel = viewModel(),
    userRepository: UserRepository,
    navController: NavController,
    onScroll: (Boolean) -> Unit = { _ -> } // true = show, false = hide
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
        val typeOfPost = remember { mutableIntStateOf(0) }
        LaunchedEffect(Unit) {
            viewModel.fetchCurrentUserPosts()
            viewModel.fetchCurrentUserLikedPosts()
            viewModel.fetchCurrentUser()
            viewModel.getCurrentUserFollowers()
            viewModel.getCurrentUserFollowing()
        }

        val isLoading = viewModel.isLoading
        val error = viewModel.errorMessage
//        val user = viewModel.currentUser
        val user = TokenManager.getUser()

        val isPostsLoading = viewModel.isPostsLoading
        val postsList = viewModel.postsList
        val postsError = viewModel.postsErrorMessage

        val isLikedPostsLoading = viewModel.isLikedPostsLoading
        val likedPostsList = viewModel.likedPostsList
        val likedPostsError = viewModel.likedPostsErrorMessage

        val followersList = viewModel.followersList
        val followingList = viewModel.followingList

        posts.value = when (typeOfPost.intValue) {
            0 -> postsList
            1 -> likedPostsList
            else -> null
        }
        val blurBg = remember { mutableStateOf(false) }
        val animatedBlur by animateDpAsState(
            targetValue = if (blurBg.value) 5.dp else 0.dp,
            label = "blurTransition"
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .blur(animatedBlur)
                .background(LocalExtraColors.current.listBg)
        ) {

            item {
                if (user!=null)
                    UserInfo(
                        user = user,
                        navController,
                        followersList!!,
                        followingList!!,
                        onFollowClick = { userId, isFollowing ->
                            if (isFollowing) {
                                viewModel.unfollowUser(userId)
                                TokenManager.updateUserFollowingNo(-1)
                            } else {
                                viewModel.followUser(userId)
                                viewModel.addUserToCurrentFollowingList(userId)
                                TokenManager.updateUserFollowingNo(1)
                            }
                            viewModel.followersList?.forEach { it ->
                                if (it._id == userId) {
                                    it.isFollowing = !isFollowing
                                }
                            }
                            viewModel.followingList?.forEach { it ->
                                if (it._id == userId) {
                                    it.isFollowing = !isFollowing
                                }
                            }
                        },
                        onBlurBgChange = { blur ->
                            blurBg.value = blur
                        },
                    )
            }
            item {
                PostTypeButtons(
                    onMyPostsClick = {
                        posts.value = postsList
                    },
                    onLikedPostsClick = {
                        posts.value = likedPostsList
                    },
                    typeOfPost = typeOfPost
                )
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
                    items = posts.value!!,
                    key = { post -> post._id + "-" + post.no_of_like + "-" + post.isLiked }) { post ->
                    Post(
                        post,
                        modifier = Modifier.padding(5.dp),
                        onPostClick = { postId ->
                            navController.navigate(Routes.postDetails(postId))
                        },
                        onProfileClick = { userId ->
                            if (user?._id == userId) {
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
