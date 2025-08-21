package com.example.yoospace_android.ui.post

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.Like
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.Comment
import com.example.yoospace_android.ui.common.FormInputField
import com.example.yoospace_android.ui.common.Modal
import com.example.yoospace_android.ui.common.Post
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.theme.LocalExtraColors

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PostScreen(
    viewModel: PostViewModel = viewModel(),
    navController: NavController,
    postId: String,
    onScroll: (Boolean) -> Unit = { } // true = show, false = hide
) {
    ProtectedRoute(navController) {
        val listState = rememberLazyListState()
        var previousIndex by remember { mutableStateOf(0) }
        var previousOffset by remember { mutableStateOf(0) }
        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
                .collect { (index, offset) ->
                    val scrollingUp = index < previousIndex || offset==0||
                            (index == previousIndex && offset < previousOffset)

                    onScroll(scrollingUp) // true = show, false = hide

                    previousIndex = index
                    previousOffset = offset
                }
        }
        LaunchedEffect(Unit) {
            viewModel.getPostById(postId)
            viewModel.getCommentsByPostId(postId)
            viewModel.fetchWhoLikedPost(postId)
        }
        val isLoading = viewModel.isLoading
        val errorMessage = viewModel.errorMessage
        val post = viewModel.post
        val isCommentsLoading = viewModel.isCommentLoading
        val comments = viewModel.comments
        val commentErrorMessage = viewModel.commentErrorMessage
        val errorPostLike = viewModel.errorPostLike
        var commentContent by remember { mutableStateOf("") }
        var likeModalVisible by remember { mutableStateOf(false) }
        val likesList: List<Like> = viewModel.likesList
        Log.d("PostScreen", "Likes List: $likesList")

        LazyColumn(
            state = listState,
            modifier = Modifier
                .background(LocalExtraColors.current.listBg)
        ) {
            item {
                if (isLoading) {
                    Text("Loading post...")
                } else if (errorMessage != null) {
                    Text("Error: $errorMessage")
                } else if (post != null) {
                    Post(
                        post = post,
                        modifier = Modifier.padding(5.dp),
                        onPostClick = {},
                        onProfileClick = { userId ->
                            if (TokenManager.getUserId() == userId) {
                                navController.navigate(Routes.PROFILE)
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
                        showLikeModal = { likeModalVisible = true }
                    )
                } else {
                    Text("Post not found")
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(.7f)
                    ) {
                        FormInputField(
                            value = commentContent,
                            onValueChange = { commentContent = it },
                            label = "Add a comment",
                        )
                    }

                    Button(
                        onClick = {
                            if (commentContent.isNotEmpty()) {
                                viewModel.commentOnPost(postId, commentContent)
                                commentContent = ""
                            }
                        },
                        enabled = !viewModel.isCommenting,
                        modifier = Modifier,
                        contentPadding = PaddingValues(0.dp)

                    ) {
                        Text(
                            text = "Comment",
                            modifier = Modifier
                                .padding(4.dp)
                                .background(
                                    if (commentContent.isEmpty()) MaterialTheme.colorScheme.primary.copy(
                                        .6f
                                    ) else MaterialTheme.colorScheme.primary
                                )
                                .padding(8.dp),
                            color = LocalExtraColors.current.textPrimary,

                            )
                    }


                }

            }

            items(comments?.size ?: 0) { index ->
                val comment = comments?.get(index)
                if (comment != null) {
                    Comment(comment, onLikeClick = { commentId, isLiked ->
                        if (isLiked) {
                            viewModel.unlikeComment(commentId)
                        } else {
                            viewModel.likeComment(commentId)
                        }
                    }, onProfileClick = { userId ->
                        if (TokenManager.getUserId() == userId) {
                            navController.navigate(Routes.PROFILE)
                        } else
                            navController.navigate(Routes.userProfile(userId))
                    })
                }

            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }


        }

        if (likeModalVisible && likesList.isNotEmpty()) {
            Modal(
                onDismiss = {
                    likeModalVisible = false
                },
                modifier = Modifier.fillMaxHeight(),
            ) {
                LazyColumn(
                    modifier = Modifier
                ) {
                    item { Text("User Likes", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(start = 20.dp, bottom = 20.dp)) }
                    items(
                        items = likesList,
                        key = { it._id }
                    ) { like ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(LocalExtraColors.current.listBg)
                                .padding(10.dp)
                                .clickable{
                                    likeModalVisible = false
                                    if (TokenManager.getUserId() == like.usersLiked._id) {
                                        navController.navigate(Routes.PROFILE)
                                    } else {
                                        navController.navigate(Routes.userProfile(like.usersLiked._id))
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ProfileImage(
                                profileImage = ImageSource.Url(like.usersLiked.profile_image),
                                userId = like.usersLiked._id,
                                size = 35,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Text(
                                "@${like.usersLiked.userName}",
                                color = LocalExtraColors.current.textSecondary,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            )
                        }

                    }
                }
            }
        }
    }
}