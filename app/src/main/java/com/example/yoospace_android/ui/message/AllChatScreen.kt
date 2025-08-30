package com.example.yoospace_android.ui.message

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.yoospace_android.R
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.model.ConversationItemModel
import com.example.yoospace_android.data.model.ConversationUserParcel
import com.example.yoospace_android.data.model.GroupDetailsParcel
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.common.Modal
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.common.RequestTimedOut
import com.example.yoospace_android.ui.message.components.AddGroupDialog
import com.example.yoospace_android.ui.message.components.ConversationItem
import com.example.yoospace_android.ui.shimmer_componenets.AllChatShimmer
import com.example.yoospace_android.ui.theme.LocalExtraColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllChatScreen(
    viewModel: AllChatsViewModel,
    navController: NavController,
) {
    ProtectedRoute(navController) {
        val followersList = viewModel.followersList
        var showAddGroupDialog by remember { mutableStateOf(false) }
        var showInvitesModal by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            viewModel.getAllChats()
            viewModel.getAllInvites()
        }
        val loggedInUserId = TokenManager.getUserId()
        val isLoading = viewModel.isLoading
        val convoList: List<ConversationItemModel> = viewModel.convoList
        val context = LocalContext.current

        val animatedBlur by animateDpAsState(
            targetValue = if (showAddGroupDialog || showInvitesModal) 5.dp else 0.dp,
            label = "blurTransition"
        )
        val pullState = rememberPullToRefreshState()

        // --- lottie loader setup ---
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cat_mark_loading))

        // Calculate offset for pushing content down
        val indicatorHeight = 80.dp
        val offsetY = if (viewModel.isRefreshing) {
            indicatorHeight
        } else {
            (pullState.distanceFraction * indicatorHeight.value).dp
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Lottie animation at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(indicatorHeight)
                    .graphicsLayer {
                        translationY =
                            if (viewModel.isRefreshing) 0f else -(indicatorHeight.toPx() - offsetY.toPx())
                    },
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.isRefreshing) {
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.height(60.dp)
                    )
                } else if (pullState.distanceFraction > 0f) {
                    val progress = pullState.distanceFraction.coerceIn(0f, 1f)
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.height(60.dp)
                    )
                }
            }

            if (isLoading) {
                AllChatShimmer()
            } else if (viewModel.errorMessage == "408") {
                RequestTimedOut {
                    viewModel.getAllChats()
                    viewModel.getAllInvites()
                }
            } else if (convoList.isEmpty()) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_state_ghost))
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.height(200.dp)
                )
                Text(
                    text = "No Chats Yet :(",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    textAlign = TextAlign.Center
                )
            } else if (viewModel.errorMessage != "")
                PullToRefreshBox(
                    state = pullState,
                    isRefreshing = viewModel.isRefreshing,
                    onRefresh = {
                        viewModel.getAllChats(true)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = offsetY.toPx()
                        },
                    indicator = { /* Empty indicator since we handle it manually */ }
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(animatedBlur)

                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 5.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        showInvitesModal = true
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier
                                        .height(30.dp)
                                        .padding(horizontal = 10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(.2f)
                                    )
                                ) {
                                    Text(
                                        "Check Invites",
                                        fontSize = 12.sp,
                                        color = LocalExtraColors.current.textSecondary,
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .height(20.dp)
                                    )
                                }
                                Button(
                                    onClick = { showAddGroupDialog = true },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.height(30.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(.2f)
                                    )
                                ) {
                                    Text(
                                        "+ Create Group", fontSize = 12.sp,
                                        color = LocalExtraColors.current.textSecondary,
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .height(20.dp)
                                    )
                                }
                            }
                        }
                        items(convoList, key = { it -> it._id }) { convo ->
                            val isLastMessageSeen =
                                convo.lastMessage?.seenBy?.contains(loggedInUserId) ?: true

                            if (!convo.isGroup) {
                                val otherParticipant =
                                    convo.participants.firstOrNull { it._id != loggedInUserId }

                                otherParticipant?.let { participant ->
                                    ConversationItem(
                                        userId = participant._id,
                                        avatarUrl = participant.profile_image,
                                        title = participant.userName,
                                        subtitle = convo.lastMessage?.text ?: "",
                                        isSeen = isLastMessageSeen,
                                        unseenCount = convo.unseenCount,
                                        isGroup = false,
                                        lastMessageTime = convo.lastMessage?.createdAt,
                                        onClick = {
                                            val user = ConversationUserParcel(
                                                _id = participant._id,
                                                fullName = participant.fullName,
                                                profile_image = participant.profile_image,
                                                userName = participant.userName
                                            )
                                            navController.currentBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("user", user)
                                            navController.navigate(Routes.directChat(participant._id))
                                        }
                                    )
                                }
                            } else {
                                ConversationItem(
                                    avatarUrl = convo.avatar,
                                    title = convo.groupName,
                                    subtitle = convo.lastMessage?.text ?: "",
                                    isSeen = isLastMessageSeen,
                                    unseenCount = convo.unseenCount,
                                    isGroup = true,
                                    lastMessageTime = convo.lastMessage?.createdAt,
                                    onClick = {
                                        val groupDetails = GroupDetailsParcel(
                                            _id = convo._id,
                                            groupName = convo.groupName,
                                            avatar = convo.avatar,
                                        )
                                        navController.currentBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("groupDetails", groupDetails)
                                        navController.navigate(Routes.groupChat(conversationId = convo._id))
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        item {
                            Spacer(modifier = Modifier.height(70.dp))
                        }
                    }
                }

            if (showAddGroupDialog) {
                //add dialog
                val context = LocalContext.current
                AddGroupDialog(
                    followers = followersList.collectAsState().value,
                    onDismiss = { showAddGroupDialog = false },
                    onCreateGroup = { groupName: String, avatarUri: Uri?, selectedFollowers: List<String> ->

                        viewModel.createGroup(
                            groupName = groupName,
                            avatarUri = avatarUri,
                            selectedFollowers = selectedFollowers,
                            context = context,
                            onSuccess = {
                                showAddGroupDialog = false
                            },
                            onError = { errorMessage ->
                                Toast.makeText(
                                    context,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                )
            }
            if (showInvitesModal) {
                // Show invites modal
                Modal(
                    onDismiss = {
                        showInvitesModal = false
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                ) {
                    LazyColumn {
                        item {
                            Text(
                                "Group Invites",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(start = 20.dp, bottom = 20.dp)
                            )
                        }
                        items(
                            items = viewModel.groupInvitesList,
                            key = { it._id }
                        ) { groupInvite ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(LocalExtraColors.current.item_bg)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row {
                                    ProfileImage(
                                        profileImage = if (groupInvite.group.avatar.isNotEmpty()) ImageSource.Url(
                                            groupInvite.group.avatar
                                        ) else ImageSource.LocalResource(
                                            R.drawable.group_avatar
                                        ),
                                        userId = groupInvite.group._id,
                                        size = 35,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )
                                    Column {
                                        Text(
                                            "${groupInvite.invited_by.userName} invited you to",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = LocalExtraColors.current.textSecondary,
                                            fontWeight = FontWeight.Thin
                                        )
                                        Text(
                                            groupInvite.group.groupName,
                                            color = LocalExtraColors.current.textSecondary,
                                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        viewModel.acceptInvite(
                                            groupInvite.group._id,
                                            onSuccess = {
                                                showInvitesModal = false
                                            },
                                            onError = { errorMessage ->
                                                Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color(0xFF8208FC),
                                                    Color(0xFF3725D7)
                                                )
                                            )
                                        )
                                        .height(30.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    )

                                ) {
                                    Text(
                                        "Accept",
                                        modifier = Modifier
                                            .padding(5.dp),
                                        color = Color.White,
                                    )
                                }

                            }

                        }
                    }
                }
            }
        }
    }
}