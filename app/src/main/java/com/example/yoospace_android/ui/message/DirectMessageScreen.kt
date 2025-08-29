package com.example.yoospace_android.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.yoospace_android.R
import com.example.yoospace_android.data.model.ConversationUserParcel
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.navigation.Routes
import com.example.yoospace_android.ui.common.FormInputField
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.message.components.ChatBubble
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun DirectMessageScreen(
    viewModel: DirectMessageViewModel,
    sender: ConversationUserParcel,
    navController: NavController
) {
//    val viewModel = remember { DirectMessageViewModel(targetUser = sender) }
    val messages by viewModel.messages.collectAsState()
//    val conversation = viewModel.conversation

    ProtectedRoute(navController = navController) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(Modifier) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 10.dp)
                        .clickable{
                            navController.navigate(Routes.userProfile(sender._id))
                        }

                ) {
                    ProfileImage(
                        userId = sender._id,
                        profileImage = ImageSource.Url(sender.profile_image),
                        size = 50
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = sender.userName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        color = LocalExtraColors.current.textSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
            }
            if (viewModel.messagesLoading.value){
//                show a material3 circular progress indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )

                }
                return@Column
            }
            LazyColumn(modifier = Modifier.weight(.8f), reverseLayout = true) {
                items(messages) { msg ->
                    ChatBubble(
                        message = msg.text,
                        isSentByMe = msg.sender != sender._id,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            var text by remember { mutableStateOf("") }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 5.dp)
//                    .weight(.1f)
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(.8f)
                ) {
                    FormInputField(
                        value = text,
                        onValueChange = { text = it },
                        label = "write your message...",
                    )
                }

                Button(
                    onClick = {
                        if (text.isNotEmpty()) {
                            viewModel.sendMessage(
                                text
                            )
                            text = ""
                        }
                    },
                    enabled = !viewModel.isSendingMessage,
                    modifier = Modifier.size(50.dp),
                    contentPadding = PaddingValues(0.dp)

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.send_msg),
                        contentDescription = "Send Message",
                        tint = LocalExtraColors.current.textPrimary,
                        modifier = Modifier
//                            .padding(4.dp)
                            .size(36.dp)
                            .background(
                                MaterialTheme.colorScheme.primary
                            )
//                            .padding(8.dp),
                    )
                }
            }

        }
    }
}
