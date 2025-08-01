package com.example.yoospace_android.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.Comment
import com.example.yoospace_android.ui.common.FormInputField
import com.example.yoospace_android.ui.common.Post
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun PostScreen(
    viewModel: PostViewModel = viewModel(),
    navController: NavController,
    postId: String
) {
    ProtectedRoute(navController) {
        LaunchedEffect(Unit) {
            viewModel.getPostById(postId)
            viewModel.getCommentsByPostId(postId)
        }
        val isLoading = viewModel.isLoading
        val errorMessage = viewModel.errorMessage
        val post = viewModel.post
        val isCommentsLoading = viewModel.isCommentLoading
        val comments = viewModel.comments
        val commentErrorMessage = viewModel.commentErrorMessage
        val errorPostLike = viewModel.errorPostLike
        var commentContent by remember { mutableStateOf("") }

        LazyColumn(
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
                    )
                } else {
                    Text("Post not found")
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(.7f)
                    ){
                        FormInputField(
                            value = commentContent,
                            onValueChange = { commentContent = it },
                            label = "Add a comment",
                        )
                    }

                    Button(
                        onClick = {},
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
                    Comment(comment)
                }

            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }


        }

    }
}