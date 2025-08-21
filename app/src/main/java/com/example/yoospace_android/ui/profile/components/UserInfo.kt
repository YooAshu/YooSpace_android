package com.example.yoospace_android.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.CurrentUser
import com.example.yoospace_android.data.model.FollowDetail
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.Modal
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.theme.LocalExtraColors
import com.example.yoospace_android.utils.generateGradient


@Composable
fun UserInfo(
    user: CurrentUser,
    navController: NavController,
    followersList: List<FollowDetail> = emptyList<FollowDetail>(),
    followingList: List<FollowDetail> = emptyList<FollowDetail>(),
    onFollowClick: (String, Boolean) -> Unit,
    onBlurBgChange: (Boolean) -> Unit,
    isLoading: Boolean = false
) {
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
            userId = user._id,
            profileImage = ImageSource.Url(user.profile_image),
            modifier = Modifier.constrainAs(profileImage) {
                bottom.linkTo(banner.bottom)
                start.linkTo(parent.start, margin = 20.dp)
                translationY = 60.dp
            }
        )
        if (user._id == TokenManager.getUserId()) {
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
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .clickable {
                        navController.navigate(Routes.UPDATE_PROFILE)
                    },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold

            )
        }
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
                },
            followerList = followersList,
            followingList = followingList,
            navController = navController,
            onFollowClick = onFollowClick,
            onBlurBgChange = onBlurBgChange
        )


    }

}

@Composable
fun UserStats(
    user: CurrentUser,
    modifier: Modifier = Modifier,
    navController: NavController,
    followerList: List<FollowDetail> = emptyList(),
    followingList: List<FollowDetail> = emptyList(),
    onFollowClick: (String, Boolean) -> Unit,
    onBlurBgChange: (Boolean) -> Unit
) {

    var followListModalVisible by remember { mutableStateOf(false) }
    var list by remember { mutableStateOf(emptyList<FollowDetail>()) }
    var noOfFollowing by remember { mutableIntStateOf(user.no_of_following) }
    val modalHeader = remember { mutableStateOf("Followers") }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        UserStatItem(label = "Posts", value = user.no_of_post.toString(), onClick = {})
        UserStatItem(label = "Followers", value = user.no_of_follower.toString(), onClick = {
            list = followerList
            followListModalVisible = true
            modalHeader.value = "Followers"
            onBlurBgChange(true)

        })
        UserStatItem(label = "Following", value = noOfFollowing.toString(), onClick = {
            list = followingList
            followListModalVisible = true
            modalHeader.value = "Following"
            onBlurBgChange(true)
        })
    }
    if (followListModalVisible) {
        Modal(
            onDismiss = {
                followListModalVisible = false
                onBlurBgChange(false)
            },
            modifier = Modifier.fillMaxHeight()
        ) {

            LazyColumn {
                item {
                    Text(
                        modalHeader.value,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(start = 20.dp, bottom = 20.dp)
                    )
                }
                items(
                    items = list,
                    key = { it._id }
                ) { follow ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(LocalExtraColors.current.listBg)
                            .padding(10.dp)
                            .clickable {
                                followListModalVisible = false
                                if (TokenManager.getUserId() == follow._id) {
                                    navController.navigate(Routes.PROFILE)
                                } else {
                                    navController.navigate(Routes.userProfile(follow._id))
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var isFollowing by remember { mutableStateOf(follow.isFollowing) }
                        Row {
                            ProfileImage(
                                profileImage = ImageSource.Url(follow.profile_image),
                                userId = follow._id,
                                size = 35,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Text(
                                "@${follow.userName}",
                                color = LocalExtraColors.current.textSecondary,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            )
                        }
                        if (
                            TokenManager.getUserId() != follow._id
                        )
                            Button(
                                onClick = {
                                    onFollowClick(follow._id, isFollowing)
                                    if (isFollowing) {
                                        noOfFollowing--
                                    } else {
                                        noOfFollowing++
                                    }
                                    isFollowing = !isFollowing
                                },
                                modifier = Modifier
                                    .border(1.dp, Color(0xFF6B38F8), RoundedCornerShape(20.dp))
                                    .clip(RoundedCornerShape(20.dp))
                                    .height(30.dp)
                                    .width(80.dp)
                                    .background(
                                        if (!isFollowing)
                                            SolidColor(Color.Transparent)
                                        else
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color(0xFF8208FC),
                                                    Color(0xFF3725D7)
                                                )
                                            )
                                    ),
                                contentPadding = PaddingValues(0.dp),
                                colors = MaterialTheme.colorScheme.run {
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color.White
                                    )
                                }
                            ) {
                                Text(
                                    if (isFollowing) "Following" else "Follow",
                                    modifier = Modifier
                                        .padding(5.dp)
                                )
                            }

                    }

                }
            }
        }
    }
}

@Composable
fun UserStatItem(label: String, value: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            onClick()
        }
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
