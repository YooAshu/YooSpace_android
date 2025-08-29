package com.example.yoospace_android.ui.profile

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.yoospace_android.R
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.Post
import com.example.yoospace_android.ui.common.RequestTimedOut
import com.example.yoospace_android.ui.profile.components.PostTypeButtons
import com.example.yoospace_android.ui.profile.components.UserInfo
import com.example.yoospace_android.ui.shimmer_componenets.PostShimmer

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CurrentProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController,
    onScroll: (Boolean) -> Unit = { _ -> } // true = show, false = hide
) {

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

//        val isLoading = viewModel.isLoading
//        val error = viewModel.errorMessage
        val user = viewModel.currentUser
//        val user = TokenManager.getUser()

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
                .background(MaterialTheme.colorScheme.background)
        ) {

            item {
                if (user != null)
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
            if ((typeOfPost.intValue == 0 && isPostsLoading) || (typeOfPost.intValue == 1 && isLikedPostsLoading)) {
                items(count = 3){
                    PostShimmer()
                    Spacer(Modifier.height(20.dp))
                }
            }

            item {
                if ((typeOfPost.intValue == 0 && postsError == "408") || (typeOfPost.intValue == 1 && likedPostsError == "408")) {
                    RequestTimedOut {
                        when (typeOfPost.intValue) {
                            0 -> viewModel.fetchCurrentUserPosts()
                            1 -> viewModel.fetchCurrentUserLikedPosts()
                        }
                        if(followersList?.isEmpty() == true){
                            viewModel.getCurrentUserFollowers()
                        }
                        if(followingList?.isEmpty() == true) {
                            viewModel.getCurrentUserFollowing()
                        }
                    }
                } else if (posts.value?.isEmpty() == true) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_state_ghost))
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.height(200.dp)
                    )
                    Text(
                        text = if (typeOfPost.intValue == 0) "No posts available Make Your 1st Post" else "No liked posts, Like Someone's post to see here",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (posts.value?.isNotEmpty() == true) {

                items(
                    items = posts.value!!,
                    key = { post -> post._id + "-" + post.no_of_like + "-" + post.isLiked }) { post ->
                    val isLiking = post._id in viewModel.likingPosts
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
                            if (!isLiking) {
                                if (isLiked) {
                                    viewModel.unlikePost(postId)
                                } else {
                                    viewModel.likePost(postId)
                                }
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
