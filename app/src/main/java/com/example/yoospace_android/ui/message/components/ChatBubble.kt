package com.example.yoospace_android.ui.message.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.common.ProfileImage

@Composable
fun ChatBubble(
    message: String,
    isSentByMe: Boolean,
    modifier: Modifier = Modifier,
    senderName: String = "",
    senderProfileImageSrc: String = "",
    senderId: String = "",
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
    ) {
        // If NOT me → Profile on left
        if (!isSentByMe && senderId.isNotEmpty()) {
            ProfileImage(
                userId = senderId,
                profileImage = ImageSource.Url(senderProfileImageSrc),
                size = 30
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(.8f),
            contentAlignment = if (isSentByMe) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Text(
                text = message,
                color = if (isSentByMe) Color.White else Color.Black,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .background(
                        brush = if (isSentByMe) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6608FC), // Light Blue
                                    Color(0xFF4325D7)  // Dark Blue
                                )
                            )
                        } else {
                            Brush.linearGradient(listOf(Color(0xFFE0E0E0), Color(0xFFE0E0E0)))
                        },
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomEnd = if (isSentByMe) 0.dp else 16.dp,
                            bottomStart = if (isSentByMe) 16.dp else 0.dp
                        )
                    )
                    .padding(vertical = 6.dp, horizontal = 12.dp)
            )
        }
        // If me → Profile on right
        if (isSentByMe && senderId.isNotEmpty()) {
            Spacer(modifier = Modifier.width(6.dp))
            ProfileImage(
                userId = senderId,
                profileImage = ImageSource.Url(senderProfileImageSrc),
                size = 30
            )
        }
    }
}
