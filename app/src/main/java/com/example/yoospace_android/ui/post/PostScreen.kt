package com.example.yoospace_android.ui.post

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
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
import com.example.yoospace_android.data.model.Like
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.Comment
import com.example.yoospace_android.ui.common.FormInputField
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.common.Modal
import com.example.yoospace_android.ui.common.Post
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.common.RequestTimedOut
import com.example.yoospace_android.ui.shimmer_componenets.CommentShimmer
import com.example.yoospace_android.ui.shimmer_componenets.PostShimmer
import com.example.yoospace_android.ui.theme.LocalExtraColors

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PostScreen(
    viewModel: PostViewModel,
    navController: NavController,
    postId: String,
    onScroll: (Boolean) -> Unit = { } // true = show, false = hide
) {
    ProtectedRoute(navController) {
        val listState = rememberLazyListState()
        var previousIndex by remember { mutableIntStateOf(0) }
        var previousOffset by remember { mutableIntStateOf(0) }
        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
                .collect { (index, offset) ->
                    val scrollingUp = index < previousIndex || offset == 0 ||
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
        var commentContent by remember { mutableStateOf("") }
        var likeModalVisible by remember { mutableStateOf(false) }
        val likesList: List<Like> = viewModel.likesList
        Log.d("PostScreen", "Likes List: $likesList")

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                if (isLoading) {
                    PostShimmer()
                } else if (errorMessage != null) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        Text("Error: $errorMessage", textAlign = TextAlign.Center)
                    }
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
                        modifier = Modifier.fillMaxWidth(.75f)
                    ) {
                        FormInputField(
                            value = commentContent,
                            onValueChange = { commentContent = it },
                            label = "Add a comment",
                            modifier = Modifier
                        )
                    }

                    Button(
                        onClick = {
                            if (commentContent.isNotEmpty()) {
                                viewModel.commentOnPost(postId, commentContent)
                                viewModel.incrementCommentCount()
                                commentContent = ""
                            }
                        },
                        enabled = !viewModel.isCommenting,
                        modifier = Modifier,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )

                    ) {
                        Text(
                            text = "Comment",
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    colorResource(R.color.btn_1)
                                )
                                .padding(8.dp),
                            color = LocalExtraColors.current.textPrimary,
                            fontSize = 12.sp
                            )
                    }


                }

            }
            if(isCommentsLoading){
                items(
                    count = 4
                ){
                    CommentShimmer()
                    Spacer(Modifier.height(10.dp))
                }

            }
            item {
                if (comments == null && commentErrorMessage == "408") {
                    RequestTimedOut {
                        viewModel.getCommentsByPostId(postId)
                    }
                }
                else if(comments?.isEmpty()==true){
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_state_ghost))
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.height(200.dp)
                    )
                    Text(
                        text ="No Comments yet Be the first one to comment",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        textAlign = TextAlign.Center
                    )
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
                    item {
                        Text(
                            "User Likes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(start = 20.dp, bottom = 20.dp)
                        )
                    }
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
                                .clickable {
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