package com.example.yoospace_android.ui.feed

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.yoospace_android.R
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.Post
import com.example.yoospace_android.ui.common.RequestTimedOut
import com.example.yoospace_android.ui.shimmer_componenets.PostShimmer

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: FeedViewModel,
    onScroll: (Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    var previousIndex by remember { mutableIntStateOf(0) }
    var previousOffset by remember { mutableIntStateOf(0) }
    val threshold = 30

    // --- scroll listener for header show/hide ---
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val delta = offset - previousOffset
                val scrollingUp = index < previousIndex ||
                        (index == previousIndex && delta < -threshold) ||
                        offset == 0
                val scrollingDown = index > previousIndex ||
                        (index == previousIndex && delta > threshold)

                when {
                    scrollingUp -> onScroll(true)
                    scrollingDown -> onScroll(false)
                }

                previousIndex = index
                previousOffset = offset
            }
    }

    // --- initial load ---
    LaunchedEffect(Unit) {
        viewModel.fetchFeedPosts()
    }

    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val posts by viewModel.feedPosts.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    // --- custom pull-to-refresh state ---
    val pullState = rememberPullToRefreshState()

    // --- lottie loader setup ---
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cat_mark_loading))

    // Calculate offset for pushing content down
    val indicatorHeight = 80.dp
    val offsetY = if (isRefreshing) {
        indicatorHeight
    } else {
        (pullState.distanceFraction * indicatorHeight.value).dp
    }
    ProtectedRoute(navController) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Lottie animation at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(indicatorHeight)
                    .graphicsLayer {
                        translationY =
                            if (isRefreshing) 0f else -(indicatorHeight.toPx() - offsetY.toPx())
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isRefreshing) {
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.height(60.dp)
                    )
                } else if (pullState.distanceFraction > 0f) {
                    val progress = pullState.distanceFraction.coerceIn(0f, 1f)
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.height(60.dp)
                    )
                }
            }

            // LazyColumn pushed down by offset
            PullToRefreshBox(
                state = pullState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    viewModel.refreshFeedPosts()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = offsetY.toPx()
                    },
                indicator = { /* Empty indicator since we handle it manually */ }
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if(isLoading){
                        items(count = 5){
                            PostShimmer()
                            Spacer(Modifier.height(20.dp))
                        }
                    }
                    item {
                        when {
                            errorMessage == "408" -> {
                                RequestTimedOut {
                                    viewModel.fetchFeedPosts()
                                }
                            }

                            (posts.isEmpty() && !isLoading) -> {
                                val composition by rememberLottieComposition(
                                    LottieCompositionSpec.RawRes(
                                        R.raw.empty_state_ghost
                                    )
                                )
                                LottieAnimation(
                                    composition = composition,
                                    iterations = LottieConstants.IterateForever,
                                    modifier = Modifier.height(200.dp)
                                )
                                Text(
                                    "No posts available Follow Someone to see their posts",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    items(
                        posts,
                        key = { post -> post._id + "-" + post.no_of_like + "-" + post.isLiked }
                    ) { post ->
                        val isLiking = post._id in viewModel.likingPosts
                        Post(
                            post,
                            modifier = Modifier.padding(5.dp),
                            onPostClick = { postId ->
                                navController.navigate(Routes.postDetails(postId))
                            },
                            onProfileClick = { userId ->
                                if (TokenManager.getUserId() == userId) {
                                    navController.navigate(Routes.PROFILE)
                                } else {
                                    navController.navigate(Routes.userProfile(userId))
                                }
                            },
                            onLikeClick = { postId, liked ->
                                if (!isLiking) {
                                    if (liked) viewModel.unlikePost(postId)
                                    else viewModel.likePost(postId)
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
    }
}