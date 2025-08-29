package com.example.yoospace_android.ui.post


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.yoospace_android.R
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.theme.LocalExtraColors
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun AddPostScreen(
    navController: NavController,
    viewModel: PostViewModel,
) {
    val hazeState = rememberHazeState()
    ProtectedRoute(
        navController
    ) {
        val context = LocalContext.current
        val isCreatingPost = viewModel.isCreatingPost
        val imageUris = remember { mutableStateOf<List<Uri?>>(emptyList()) }
        val croppedImageUris = remember { mutableStateOf<List<Uri?>>(emptyList()) }
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { imageUris.value.size }
        )
        val postText = remember { mutableStateOf("") }
        val selectedImageIndex = remember { mutableIntStateOf(-1) }
        val cropImage = rememberLauncherForActivityResult(
            CropImageContract()
        ) { result ->
            if (result.isSuccessful && selectedImageIndex.intValue != -1) {
                val croppedUri = result.uriContent
                if (croppedUri != null) {
                    croppedImageUris.value = croppedImageUris.value.mapIndexed { index, uri ->
                        if (index == selectedImageIndex.intValue) {
                            croppedUri // Replace the selected image with the cropped one
                        } else {
                            uri // Keep other images unchanged
                        }
                    }
                }
            }
        }
        val selectedAspectRatioIndex = remember { mutableIntStateOf(0) }
        val aspectRatios: List<AspectRatio> = listOf(
            AspectRatio(1, 1, "1:1", R.drawable.aspect_ratio_1_1),
            AspectRatio(3, 4, "4:3", R.drawable.aspect_ratio_3_4),
            AspectRatio(16, 9, "16:9", R.drawable.aspect_ratio_16_9)
        )
        var limit by remember { mutableIntStateOf(3) } // overall max
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia()
        ) { uris ->
            if (uris.isNotEmpty()) {
                val availableSlots = limit
                val toAdd = uris.take(availableSlots) // only take up to remaining limit

                imageUris.value = imageUris.value + toAdd
                croppedImageUris.value = croppedImageUris.value + toAdd
                limit -= toAdd.size
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)

        ) {
            item {
                val user = TokenManager.getUser()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProfileImage(
                            userId = user!!._id,
                            profileImage = ImageSource.Url(user.profile_image),
                            size = 50
                        )
                        Text(
                            "@${user.userName}",
                            color = LocalExtraColors.current.textSecondary,
                            modifier = Modifier
                                .padding(start = 10.dp)
                        )
                    }
                    Button(
                        onClick = {
                            val requestBodyBuilder =
                                MultipartBody.Builder().setType(MultipartBody.FORM)
                            if (postText.value.isNotEmpty()) {
                                requestBodyBuilder.addFormDataPart("content", postText.value)
                            }
                            croppedImageUris.value.forEachIndexed { index, uri ->
                                if (uri != null) {
                                    val file = File(
                                        viewModel.getRealPathFromUri(
                                            context,
                                            uri
                                        )
                                    )
                                    val requestFile = file.asRequestBody("image/*".toMediaType())
                                    requestBodyBuilder.addFormDataPart(
                                        "media",
                                        "image_$index.jpg",
                                        requestFile
                                    )
                                }
                            }
                            requestBodyBuilder.addFormDataPart(
                                "aspectRatio",
                                """{"x": ${aspectRatios[selectedAspectRatioIndex.intValue].x},
                                     "y": ${aspectRatios[selectedAspectRatioIndex.intValue].y}}"""
                            )
                            if (postText.value.isEmpty() && croppedImageUris.value.isEmpty()) {
                                // Show a message or handle the case where no content is provided
                                Toast.makeText(
                                    context,
                                    "Please add some content or an image to post.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModel.createPost(requestBodyBuilder.build(), onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Post created successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()
                                })

                            }
                        },
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = LocalExtraColors.current.textPrimary
                        ),
                        contentPadding = PaddingValues(0.dp)

                    ) {
                        Text(
                            text =
                                if (isCreatingPost) "Posting..." else "Post",
                            color = LocalExtraColors.current.textPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 20.dp, vertical = 5.dp),
                            fontSize = 14.sp
                        )
                    }

                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                )
                {
                    if (imageUris.value.isEmpty()) {
                        IconButton(
                            onClick = {
                                if (limit > 0) {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(LocalExtraColors.current.cardBackground),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceAround,

                                ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.upload_image),
                                    contentDescription = "Upload Image",
                                    tint = LocalExtraColors.current.textSecondary,
                                )
                                Text(
                                    "Upload Image",
                                    color = LocalExtraColors.current.textSecondary,
                                    modifier = Modifier
                                        .padding(top = 5.dp),
                                )

                            }
                        }
                    } else {
                        Box(
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .hazeSource(hazeState),
                                pageSpacing = 8.dp, // Small gap between pages
                                contentPadding = PaddingValues(horizontal = 32.dp) // Peek space
                            ) { page ->
                                AsyncImage(
                                    model = croppedImageUris.value[page],
                                    contentDescription = "Image $page",
                                    modifier = Modifier
                                        .fillMaxWidth(1f) // Each image takes ~85% of screen width
                                        .aspectRatio(
                                            aspectRatios[selectedAspectRatioIndex.intValue].x.toFloat() /
                                                    aspectRatios[selectedAspectRatioIndex.intValue].y.toFloat()
                                        )
                                        .clip(RoundedCornerShape(5.dp))
                                        .clickable {
                                            cropImage.launch(
                                                CropImageContractOptions(
                                                    uri = imageUris.value[page],
                                                    cropImageOptions = CropImageOptions().apply {
                                                        aspectRatioX =
                                                            aspectRatios[selectedAspectRatioIndex.intValue].x
                                                        aspectRatioY =
                                                            aspectRatios[selectedAspectRatioIndex.intValue].y
                                                        fixAspectRatio = true
                                                        showCropOverlay = true
                                                        showProgressBar = true
                                                        allowRotation = true
                                                        activityBackgroundColor =
                                                            android.graphics.Color.BLACK

                                                    }
                                                )
                                            )

                                            selectedImageIndex.intValue = page
                                        },
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Box(
                                Modifier
                                    .zIndex(2f)
                                    .padding(bottom = 10.dp)
                                    .fillMaxWidth(.7f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(100))
                                    .border(1.dp, Color.White.copy(.3f), RoundedCornerShape(100))
                                    .hazeEffect(
                                        state = hazeState, style = HazeStyle(
                                            blurRadius = 10.dp,
                                            backgroundColor = Color.Unspecified,
                                            tint = HazeTint(
                                                color = Color.White.copy(.2f),
                                                blendMode = BlendMode.Lighten
                                            ),
                                            noiseFactor = .1f
                                            )
                                    )
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            val event = awaitPointerEvent()
                                            event.changes.forEach { it.consume() }
                                        }
                                    }
                                    .clickable {
                                        if (limit > 0) {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                        else{
                                            Toast.makeText(context, "Max 3 images", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Add More Images",
                                    fontWeight = FontWeight.Medium
                                )
                            }

                        }
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(100))
                                .background(LocalExtraColors.current.cardBackground)
                        ) {
                            val isAspectRatioSet = remember { mutableStateOf(true) }
                            Icon(
                                painter = painterResource(id = R.drawable.aspect_ratio),
                                contentDescription = "aspect-ratio",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp)
                                    .clickable {
                                        isAspectRatioSet.value = !isAspectRatioSet.value
                                    }
                            )
                            if (!isAspectRatioSet.value) {
                                aspectRatios.forEachIndexed { index, aspectRatio ->
                                    Icon(
                                        painter = painterResource(id = aspectRatio.icon),
                                        contentDescription = aspectRatio.contentDescription,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .padding(5.dp)
                                            .clickable {
                                                isAspectRatioSet.value = !isAspectRatioSet.value
                                                selectedAspectRatioIndex.intValue = index
                                            },
                                        tint = if (index == selectedAspectRatioIndex.intValue) {
                                            Color(0xFF6200EE) // Highlight selected aspect ratio
                                        } else {
                                            LocalExtraColors.current.textSecondary
                                        }
                                    )
                                }

                            }

                        }
                    }
                }
            }
            item {
                TextField(
                    value = postText.value,
                    onValueChange = { postText.value = it },
                    placeholder = {
                        Text(
                            text = "Write something...",
                            color = LocalExtraColors.current.textSecondary
                        )
                    },
                    maxLines = 4,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                }

            }

        }

    }
}

data class AspectRatio(val x: Int, val y: Int, val contentDescription: String, val icon: Int)