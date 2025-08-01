package com.example.yoospace_android.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.yoospace_android.R
import com.example.yoospace_android.data.model.Comment
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun Comment(
    comment: Comment
) {
    var isLiked by remember { mutableStateOf(comment.isLiked) }
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(all = 10.dp),
    ) {
        Row(

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()


        ) {
            Row {
                ProfileImage(
                    profileImage = comment.user.profile_image,
                    userName = comment.user.userName,
                    size = 35,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Text("@${comment.user.userName}", color = LocalExtraColors.current.textSecondary)
            }

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
                        isLiked = !isLiked
//                        noOfLike += if (isLiked) 1 else -1
                        // Handle like action here, e.g., update the post's like count
                    }
                ,
                tint = Color.Unspecified
            )

        }
        Text(
            comment.content,
            modifier = Modifier.padding(top = 5.dp, start = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = LocalExtraColors.current.textSecondary
        )

    }


}