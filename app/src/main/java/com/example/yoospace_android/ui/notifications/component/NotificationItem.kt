package com.example.yoospace_android.ui.notifications.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoospace_android.R
import com.example.yoospace_android.data.model.Notification
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.theme.LocalExtraColors
import com.example.yoospace_android.utils.formatPostDate


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationItem(notification: Notification, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                onClick()
            },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = LocalExtraColors.current.item_bg
        )
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (notification.type != "group_invite") {
                ProfileImage(
                    userId = notification.userId!!,
                    profileImage = ImageSource.Url(notification.image!!),
                    size = 48
                )
            } else {
                ProfileImage(
                    userId = notification.groupId!!,
                    profileImage = if (notification.image == "") ImageSource.LocalResource(R.drawable.group_avatar) else ImageSource.Url(
                        notification.image!!
                    ),
                    size = 48
                )
            }

            Column {

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = formatPostDate(notification.createdAt),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp
                )
            }

        }


    }
}
