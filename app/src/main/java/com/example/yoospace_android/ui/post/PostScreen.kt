package com.example.yoospace_android.ui.post

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.ui.components.Comment
import com.example.yoospace_android.ui.components.Post
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
                            if(TokenManager.getUserId()==userId) {
                                navController.navigate("profile")
                            } else
                            navController.navigate("user_profile/$userId")
                        }
                    )
                } else {
                    Text("Post not found")
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