package com.example.yoospace_android.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.theme.LocalExtraColors
import com.example.yoospace_android.ui.common.Post
import com.example.yoospace_android.ui.profile.components.PostTypeButtons
import com.example.yoospace_android.ui.profile.components.UserInfo

@Composable
fun CurrentProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    navController: NavController,
) {
    ProtectedRoute(navController = navController) {
        val posts = remember { mutableStateOf<List<Post>?>(null) }
        val typeOfPost = remember { mutableIntStateOf(0) }
        LaunchedEffect(Unit) {
            viewModel.fetchCurrentUser()
            viewModel.fetchCurrentUserPosts()
            viewModel.fetchCurrentUserLikedPosts()
        }

        val isLoading = viewModel.isLoading
        val error = viewModel.errorMessage
        val user = viewModel.currentUser

        val isPostsLoading = viewModel.isPostsLoading
        val postsList = viewModel.postsList
        val postsError = viewModel.postsErrorMessage

        val isLikedPostsLoading = viewModel.isLikedPostsLoading
        val likedPostsList = viewModel.likedPostsList
        val likedPostsError = viewModel.likedPostsErrorMessage

        posts.value = when (typeOfPost.intValue) {
            0 -> postsList
            1 -> likedPostsList
            else -> null
        }

        when {
            isLoading -> {
                Text("Loading...")
            }

            error != null -> {
                Text("Error: $error")
            }

            user != null -> {
                LazyColumn(
                    modifier = Modifier
                        .background(LocalExtraColors.current.listBg)
                ) {
                    item { UserInfo(user) }
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

                        items(posts.value!!.size) { index ->
                            val post = posts.value!![index]
                            Post(
                                post,
                                modifier = Modifier.padding(5.dp),
                                onPostClick = { postId ->
                                    navController.navigate(Routes.postDetails(postId))
                                },
                                onProfileClick = { userId ->
                                    if (user._id == userId) {
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
    }
}
