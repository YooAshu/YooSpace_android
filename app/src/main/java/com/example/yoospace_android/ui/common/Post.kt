package com.example.yoospace_android.ui.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoospace_android.R
import com.example.yoospace_android.data.model.Post
import com.example.yoospace_android.data.model.PostAspectRatio
import com.example.yoospace_android.ui.theme.LocalExtraColors
import com.example.yoospace_android.utils.formatPostDate


@RequiresApi(Build.VERSION_CODES.O)
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
                colorResource(R.color.photo_card_bg),
                RoundedCornerShape(35.dp)
            )
            .padding(5.dp)
            .clickable {
                onPostClick(post._id)
            }

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .padding(end = 10.dp)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            val event = awaitPointerEvent()
                            event.changes.forEach { it.consume() }
                        }
                    }
                    .clickable {
                        onProfileClick(post.creator._id)
                    }
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
            Text(
                text = formatPostDate(post.createdAt),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = MaterialTheme.colorScheme.primary.copy(.7f)
                ),
                modifier = Modifier.padding(start = 10.dp)
            )

        }

        if (post.content.isNotEmpty())
            Text(
                text = post.content,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W300,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp)
            )

        val aspectRatio = post.aspectRatio ?: PostAspectRatio(1, 1)
        if (post.media.isNotEmpty())
            Box(
                contentAlignment = Alignment.BottomStart,
                modifier = Modifier
                    .padding(top = 10.dp)
            ) {
                SwipeableImagePager(
                    imageUrls = imageUrls.ifEmpty { post.media },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio.x.toFloat() / aspectRatio.y.toFloat())
                )
            }

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(100))
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,

            ) {
            Row(
                modifier = Modifier
                    .padding(end = 15.dp)
                    .clip(RoundedCornerShape(100))
                    .background(if (isLiked) Color(0xFFFF0059) else Color.Transparent)
                    .padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isLiked) {
                        painterResource(id = R.drawable.liked)
                    } else {
                        painterResource(id = R.drawable.not_liked)
                    },
                    contentDescription = "Like",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(28.dp)
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
                    tint = if (!isLiked) MaterialTheme.colorScheme.primary else Color.Unspecified
                )
                Text(
                    text = "$noOfLike ${if (noOfLike > 1) "Likes" else "Like"}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (!isLiked) MaterialTheme.colorScheme.primary else Color.White
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


            }

            Row(
                modifier = Modifier
                    .padding(5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(id = R.drawable.comment_icon),
                    contentDescription = "Like",
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${post.no_of_comment}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier

                )

            }
        }


    }
}
