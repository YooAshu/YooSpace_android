package com.example.yoospace_android.ui.message.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.yoospace_android.R
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun ConversationItem(
    avatarUrl: String,
    title: String,
    subtitle: String,
    isSeen: Boolean,
    unseenCount: Int,
    onClick: () -> Unit,
    userId: String = "",
    isGroup: Boolean = false
) {
    val profileImage =
        if (isGroup) {
            if (avatarUrl == "") ImageSource.LocalResource(R.drawable.group_avatar) else ImageSource.Url(
                avatarUrl
            )
        } else {
            ImageSource.Url(avatarUrl)
        }
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(LocalExtraColors.current.cardBackground.copy(alpha = .5f))
            .padding(5.dp)
            .clickable{
                onClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            ProfileImage(
                userId = userId,
                profileImage = profileImage,
                size = 50
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Column {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                // Subtitle
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if(!isSeen)
                        LocalExtraColors.current.textSecondary
                    else
                        LocalExtraColors.current.textSecondary.copy(alpha = 0.7f),
                    fontWeight = if(!isSeen)
                        FontWeight.Bold
                    else
                        FontWeight.Normal
                )
            }
        }

        // Unseen count
        if (unseenCount > 0) {
            Text(
                textAlign = TextAlign.Center,
                text = "$unseenCount",
                style = MaterialTheme.typography.titleMedium,
                color = LocalExtraColors.current.textPrimary,
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(100))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}