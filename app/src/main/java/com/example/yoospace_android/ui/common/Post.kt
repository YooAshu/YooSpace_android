package com.example.yoospace_android.ui.common

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoospace_android.R
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.data.model.PostAspectRatio


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Post(
    post: Post,
    modifier: Modifier,
    onPostClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onLikeClick: (String, Boolean) -> Unit,
    imageUrls: List<String> = emptyList(),
    showLikeModal: () -> Unit = { onPostClick(post._id) }
) {
    var isLiked by remember { mutableStateOf(post.isLiked) }
    var noOfLike by remember { mutableIntStateOf(post.no_of_like) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .background(
                MaterialTheme.colorScheme.background,
                RoundedCornerShape(25.dp)
            )
            .padding(10.dp)
            .clickable {
                onPostClick(post._id)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        val event = awaitPointerEvent()
                        event.changes.forEach { it.consume() }
                    }
                }
                .clickable {
                    onProfileClick(post.creator._id)
                },
        ) {
            ProfileImage(
                size = 40,
                userId = post.creator._id,
                profileImage = ImageSource.Url(post.creator.profile_image),
                modifier = Modifier.padding(5.dp)
            )
            Text(
                text = "@${post.creator.userName}",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        if (post.content.isNotEmpty())
            Text(
                text = post.content,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(vertical = 5.dp)
            )
        val aspectRatio = post.aspectRatio ?: PostAspectRatio(1, 1)
        Log.d("Post", "Media: $post")
        if (post.media.isNotEmpty()) {
            SwipeableImagePager(
                imageUrls = imageUrls.ifEmpty { post.media },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio.x.toFloat() / aspectRatio.y.toFloat())
            )

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .height(1.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(.3f))

        )
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$noOfLike Likes",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(5.dp)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                val event = awaitPointerEvent()
                                event.changes.forEach { it.consume() }
                            }
                        }
                        .clickable {
                            showLikeModal()

                        }
                )
                Icon(
                    if (isLiked) {
                        painterResource(id = R.drawable.liked)
                    } else {
                        painterResource(id = R.drawable.not_liked)
                    },
                    contentDescription = "Like",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(24.dp)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                val event = awaitPointerEvent()
                                event.changes.forEach { it.consume() }
                            }
                        }
                        .clickable {
                            onLikeClick(post._id, isLiked)
                            isLiked = !isLiked
                            noOfLike += if (isLiked) 1 else -1

                        },
                    tint = Color.Unspecified
                )

            }

            Text(
                text = "${post.no_of_comment} Comments",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f)
            )
        }
    }
}