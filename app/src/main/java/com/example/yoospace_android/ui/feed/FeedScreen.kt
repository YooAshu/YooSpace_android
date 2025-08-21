package com.example.yoospace_android.ui.feed

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.Post
import com.example.yoospace_android.ui.theme.LocalExtraColors

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel = viewModel(),
    navController: NavController,
    onScroll: (Boolean) -> Unit
) {
    ProtectedRoute(navController) {
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




        LaunchedEffect(Unit) {
            // Fetch feed posts when the screen is first composed
            viewModel.fetchFeedPosts()
        }

        val isLoading = viewModel.isLoading
        val errorMessage = viewModel.errorMessage
        val posts = viewModel.feedPosts





        LazyColumn(
            state = listState,
            modifier = Modifier
                .background(LocalExtraColors.current.listBg)
        ) {
            item {
                if (isLoading) {
                    // Show loading indicator
                    Text("Loading posts...")
                } else if (errorMessage != null) {
                    // Show error message
                    Text("Error: $errorMessage")
                } else if (posts.isEmpty()) {
                    Text("No posts available")
                }
            }

            items(
                posts,
                key = { post -> post._id + "-" + post.no_of_like + "-" + post.isLiked }) { post ->
                Post(
                    post,
                    modifier = Modifier.padding(5.dp),
                    onPostClick = { postId ->
                        // Handle post click, e.g., navigate to post details
                        navController.navigate(Routes.postDetails(postId))
                    },
                    onProfileClick = { userId ->
                        if (TokenManager.getUserId() == userId) {
                            navController.navigate(Routes.PROFILE)
                        } else
                            navController.navigate(Routes.userProfile(userId))
                    },
                    onLikeClick = { postId, isLiked ->
                        // Handle like click
                        if (isLiked) {
                            viewModel.unlikePost(postId)
                        } else {
                            viewModel.likePost(postId)
                        }
                    }

                )

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