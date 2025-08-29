package com.example.yoospace_android.ui.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.yoospace_android.R
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun Header(
    visible: Boolean,
    onLogOutClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val targetHeight = if (visible) 50.dp else 0.dp
    val heightAnim by animateDpAsState(targetValue = targetHeight, label = "heightAnim")

    var showLogoutDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightAnim) // height collapses to 0dp
            .background(LocalExtraColors.current.header)
            .padding(horizontal = 16.dp)
            .zIndex(2f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.yoospace2),
                contentDescription = "Yoospace Logo",
                modifier = Modifier
            )
//            Text(text = "Yoo Space", color = Color(0xFF870EFF), fontWeight = FontWeight.Bold)
            Image(
                painter = painterResource(id = R.drawable.yoospace_text),
                contentDescription = "Yoospace Logo",
                modifier = Modifier.height(20.dp)
            )
        }

        Row(Modifier.wrapContentWidth()) {

            IconButton(
                onClick = { onNotificationsClick() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                ),
                modifier = Modifier.padding(7.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.notification_icon),
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(36.dp),
                    colorFilter = ColorFilter.tint(LocalExtraColors.current.textSecondary)
                )
            }
            IconButton(
                onClick = { showLogoutDialog = true },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                ),
                modifier = Modifier.padding(7.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = "Logout",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(36.dp),
                    colorFilter = ColorFilter.tint(LocalExtraColors.current.textSecondary)
                )
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = "Logout", color = LocalExtraColors.current.textSecondary)
            },
            text = {
                Text(text = "Are you sure you want to logout?",color = LocalExtraColors.current.textSecondary)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogOutClick()
                    }
                ) {
                    Text("Yes",color = LocalExtraColors.current.textSecondary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel",color = LocalExtraColors.current.textSecondary)
                }
            },
            containerColor = LocalExtraColors.current.listBg
        )
    }
}