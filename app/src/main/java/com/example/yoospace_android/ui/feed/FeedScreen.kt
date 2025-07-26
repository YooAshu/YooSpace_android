package com.example.yoospace_android.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.components.Post
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = viewModel(),
    navController: NavController
) {
    ProtectedRoute(navController) {

        val isLoading = viewModel.isLoading
        val errorMessage = viewModel.errorMessage


        LaunchedEffect(Unit) {
            // Fetch feed posts when the screen is first composed
            viewModel.fetchFeedPosts()
        }

        val posts = viewModel.feedPosts

        LazyColumn(
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
                } else if (posts?.isEmpty() == true) {
                    Text("No posts available")
                }
            }
            items(posts?.size ?: 0) { index ->
                val post = posts?.get(index)
                if (post != null) {
                    Post(
                        posts[index],
                        modifier = Modifier.padding(5.dp),
                        onPostClick = {postId->
                            // Handle post click, e.g., navigate to post details
                            navController.navigate(Routes.postDetails(postId))
                        },
                        onProfileClick = { userId ->
                            if(TokenManager.getUserId()==userId) {
                                navController.navigate("profile")
                            } else
                                navController.navigate("user_profile/$userId")
                        }

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