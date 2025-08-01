package com.example.yoospace_android.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import com.example.yoospace_android.data.model.CurrentUser
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.theme.LocalExtraColors
import com.example.yoospace_android.utils.generateGradient


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
