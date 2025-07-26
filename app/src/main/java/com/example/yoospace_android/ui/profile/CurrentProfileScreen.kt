package com.example.yoospace_android.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.yoospace_android.data.model.CurrentUser
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.components.ProfileImage
import com.example.yoospace_android.ui.theme.LocalExtraColors
import com.example.yoospace_android.utils.generateGradient
import com.example.yoospace_android.ui.components.Post

@Composable
fun CurrentProfileScreen(
    viewModel: ProfileViewModel = viewModel(), navController: NavController
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
                                    if(user._id== userId) {
//                                        navController.navigate("profile")
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
    }
}

@Composable
fun UserInfo(user: CurrentUser) {
    val userGradient = generateGradient(user.userName)
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val (banner, profileImage, updateProfile, nameBio) = createRefs()
        AsyncImage(
            model = user.cover_image,
            contentDescription = "Cover Image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 1.5f)
                .background(userGradient)
                .constrainAs(banner) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Crop

        )

        ProfileImage(
            size = 120,
            userName = user.userName,
            profileImage = user.profile_image,
            modifier = Modifier.constrainAs(profileImage) {
                bottom.linkTo(banner.bottom)
                start.linkTo(parent.start, margin = 20.dp)
                translationY = 60.dp
            }
        )
        Text(
            text = "Update Profile",
            color = LocalExtraColors.current.textPrimary,
            modifier = Modifier
                .constrainAs(updateProfile) {
                    top.linkTo(banner.bottom, margin = 10.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                }
                .clip(shape = RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold

        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(nameBio) {
                    top.linkTo(profileImage.bottom, margin = 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    translationY = 60.dp
                },
        ) {
            Row(horizontalArrangement = Arrangement.Start) {
                Text(
                    text = user.fullName,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )
                Text(
                    text = "@${user.userName}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )
            }
            Text(
                text = user.bio,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
        }
        UserStats(
            user,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .constrainAs(createRef()) {
                    top.linkTo(nameBio.bottom, margin = 40.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )


    }
}

@Composable
fun UserStats(user: CurrentUser, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        UserStatItem(label = "Posts", value = user.no_of_post.toString())
        UserStatItem(label = "Followers", value = user.no_of_follower.toString())
        UserStatItem(label = "Following", value = user.no_of_following.toString())
    }
}

@Composable
fun UserStatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            fontSize = 12.sp,
        )
    }
}

@Composable
fun PostTypeButtons(
    onMyPostsClick: () -> Unit, onLikedPostsClick: () -> Unit,
    typeOfPost: MutableIntState
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 30.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    typeOfPost.intValue = 0
                    onMyPostsClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "My Posts",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(10.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .padding(horizontal = 30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(if (typeOfPost.intValue == 0) 1f else 0.1f))
            )

        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    typeOfPost.intValue = 1
                    onLikedPostsClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Liked Posts",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(10.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .padding(horizontal = 30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(if (typeOfPost.intValue == 1) 1f else 0.1f))
            )

        }


    }
}
