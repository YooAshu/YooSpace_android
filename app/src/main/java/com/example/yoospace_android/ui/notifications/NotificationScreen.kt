package com.example.yoospace_android.ui.notifications

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.yoospace_android.R
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.RequestTimedOut
import com.example.yoospace_android.ui.notifications.component.NotificationItem


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    navController: NavController
) {
    val notifications by viewModel.notifications.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

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

    ProtectedRoute(navController = navController) { }
    Box(
        modifier = Modifier
            .fillMaxSize()
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
        when {
            loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            error == "408" -> {
                RequestTimedOut {
                    viewModel.getNotifications(true)
                }
            }

            notifications.isEmpty() -> {
                Text(
                    text = "No notifications yet",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                PullToRefreshBox(
                    state = pullState,
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        viewModel.getNotifications(true)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = offsetY.toPx()
                        },
                    indicator = { /* Empty indicator since we handle it manually */ }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications) { notif ->
                            NotificationItem(
                                notif,
                                onClick = {
                                    when (notif.type) {
                                        "group_invite" -> {
                                            navController.navigate(Routes.MESSAGES)
                                        }
                                        "follow" -> {
                                            navController.navigate(Routes.userProfile(notif.userId!!))
                                        }
                                        "reaction" -> {
                                            navController.navigate(Routes.postDetails(notif.postId!!))
                                        }
                                        "comment" -> {
                                            navController.navigate(Routes.postDetails(notif.postId!!))
                                        }
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

}
