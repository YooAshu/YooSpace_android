package com.example.yoospace_android.ui.message.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.yoospace_android.R
import com.example.yoospace_android.data.model.FollowDetail
import com.example.yoospace_android.ui.common.FormInputField
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.theme.LocalExtraColors



@Composable
fun AddGroupDialog(
    followers: List<FollowDetail>,
    onDismiss: () -> Unit,
    onCreateGroup: (String,Uri?,List<String>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFollowers by remember { mutableStateOf(listOf<String>()) }
    var creatingGroup by remember { mutableStateOf(false) }

    // Image picker launcher
    val cropImage = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful ) {
            val croppedUri = result.uriContent
            if (croppedUri != null) {
                avatarUri = croppedUri
            }
        }
    }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        avatarUri = uri
        cropImage.launch(
            CropImageContractOptions(
                uri = avatarUri,
                cropImageOptions = CropImageOptions().apply {
                    aspectRatioX =1
                    aspectRatioY =1
                    fixAspectRatio = true
                    showCropOverlay = true
                    showProgressBar = true
                    activityBackgroundColor =
                        android.graphics.Color.BLACK
                }
            )
        )
    }


    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = LocalExtraColors.current.cardBackground
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar + Edit button
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            avatarUri ?: R.drawable.group_avatar
                        ),
                        contentDescription = "Group Avatar",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.4f)),
                        contentScale = ContentScale.Crop
                    )
                    TextButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            "Edit",
                            color = Color.White,
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            modifier = Modifier
                                .border(1.dp, Color.White, CircleShape)
                                .padding(horizontal = 10.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Group name input
                FormInputField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = "Group Name"
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Add Your Followers",
                    color = LocalExtraColors.current.textSecondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.fillMaxHeight(.4f)) {
                    item {
                        FollowersSelector(
                            followers = followers,
                            selectedFollowers = selectedFollowers,
                            onToggleSelect = { id ->
                                selectedFollowers = if (selectedFollowers.contains(id)) {
                                    selectedFollowers - id
                                } else {
                                    selectedFollowers.plus(id)
                                }
                            }
                        )
                    }
                }


                Spacer(Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (groupName.isBlank()) {
                                // Show error toast/snackbar in real app
                                return@Button
                            }
                            if (selectedFollowers.isEmpty()) {
                                // Show error toast/snackbar in real app
                                return@Button
                            }
                            creatingGroup = true
                            onCreateGroup(groupName, avatarUri, selectedFollowers)
//                            creatingGroup = false
//                            onDismiss()
                        },
                        enabled = !creatingGroup
                    ) {
                        Text(if(creatingGroup)"Creating..." else "Create")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FollowersSelector(
    followers: List<FollowDetail>,
    selectedFollowers: List<String>,
    onToggleSelect: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.6f)
            .padding(top = 8.dp),
        maxItemsInEachRow = Int.MAX_VALUE,
        horizontalArrangement = Arrangement.Start,
        verticalArrangement = Arrangement.Top
    ) {
        followers.forEach { follower ->
            val isSelected = selectedFollowers.contains(follower._id)

            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(50))
                    .border(
                        1.dp,
                        if (isSelected) Color.Gray else Color.DarkGray,
                        shape = CircleShape
                    )
                    .background(if (isSelected) Color(0x54BA6DF1) else Color.Transparent)
                    .clickable { onToggleSelect(follower._id) }
                    .padding(start = 6.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileImage(
                    userId = follower._id,
                    profileImage = ImageSource.Url(follower.profile_image),
                    size = 30,
                )
                Spacer(Modifier.width(6.dp))
                Text(follower.userName, color = Color.White)
            }
        }
    }

}