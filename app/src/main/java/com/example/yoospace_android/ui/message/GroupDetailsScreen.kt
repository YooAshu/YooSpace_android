package com.example.yoospace_android.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.GroupConversationData
import com.example.yoospace_android.data.model.Member
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.common.ProfileImage

@Composable
fun GroupDetailsScreen(
    navController: NavController,
    groupDetails: GroupConversationData,
) {
    val conversation = groupDetails.conversation
    val members = groupDetails.members
    ProtectedRoute(navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Group Avatar + Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = conversation.avatar,
                    contentDescription = "Group Avatar",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = conversation.groupName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${members.size} members",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            // Members List
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "Members",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(members) { member ->
                    if (member.status == "joined")
                        MemberItem(member, onClick = {
                            if (member.member._id== TokenManager.getUserId()) {
                                navController.navigate(Routes.PROFILE)
                            }
                            else {
                                navController.navigate(Routes.userProfile(member.member._id))
                            }
                        })
                }
                item {
                    Text(
                        text = "Pending Joins",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = 8.dp, top = 40.dp)
                    )
                }

                items(members) { member ->
                    if (member.status == "invited")
                        MemberItem(member, onClick = {
                            if (member.member._id== TokenManager.getUserId()) {
                                navController.navigate(Routes.PROFILE)
                            }
                            else {
                                navController.navigate(Routes.userProfile(member.member._id))
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun MemberItem(member: Member,
               onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable{
                onClick()
            }
    ) {
        ProfileImage(
            userId = member.member._id,
            profileImage = ImageSource.Url(member.member.profile_image),
            size = 40,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = member.member.fullName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "@${member.member.userName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Show role (like admin, member etc.)
        if (member.status != "invited") {
            Text(
                text = member.role.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (member.role == "owner" || member.role == "admin") {
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF8208FC),
                                    Color(0xFF3725D7)
                                )
                            )
                        } else {
                            SolidColor(Color.Transparent) // Solid brush for non-admins
                        }
                    )

                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
