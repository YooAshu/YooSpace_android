package com.example.yoospace_android.ui.profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.data.repository.UserRepository
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.ui.common.FormInputField
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.theme.LocalExtraColors
import com.example.yoospace_android.utils.generateGradient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@Composable
fun UpdateProfileScreen(
    userRepository: UserRepository,
    navController: NavController,
) {
    val viewModel = remember { ProfileViewModel(userRepository) }

    val user = TokenManager.getUser()
    var profileImageUri: Uri? = null
    var profileImageSource by remember { mutableStateOf<ImageSource>(ImageSource.Url(user!!.profile_image)) }
    var coverImageUri: Uri? = null
    var coverImageSource by remember { mutableStateOf<ImageSource>(ImageSource.Url(user!!.cover_image)) }
    val formFields = remember {
        mutableStateMapOf(
            "userName" to user!!.userName,
            "fullName" to user.fullName,
            "bio" to user.bio
        )
    }
    var isSelectingProfileImage = false
    val userGradient = generateGradient(user!!._id)

    val cropImage = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful ) {
            val croppedUri = result.uriContent
            if (croppedUri != null) {
                if (isSelectingProfileImage) {
                    profileImageUri = croppedUri
                    profileImageSource = ImageSource.Local(croppedUri)
                } else {
                    coverImageUri = croppedUri
                    coverImageSource = ImageSource.Local(croppedUri)
                }
            }
        }
    }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {

            if (isSelectingProfileImage) {
                profileImageUri = uri
                profileImageSource = ImageSource.Local(uri)
            } else {
                coverImageUri = uri
                coverImageSource = ImageSource.Local(uri)
            }
            cropImage.launch(
            CropImageContractOptions(
                uri = uri,
                cropImageOptions = CropImageOptions().apply {
                    aspectRatioX = if (isSelectingProfileImage) 1 else 8
                    aspectRatioY = if (isSelectingProfileImage) 1 else 3
                    fixAspectRatio = true
                    showCropOverlay = true
                    showProgressBar = true
                    activityBackgroundColor =
                        android.graphics.Color.BLACK
                }
            )
        )
    }
    }

    val isUpdatingProfile = viewModel.isUpdatingProfile
    val toastMessage = viewModel.toastMessage
    val context = LocalContext.current
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.toastMessage = null // reset so it doesn't repeat
        }
    }

    ProtectedRoute(navController) {
        ConstraintLayout {
            val (cover, profile, form, updateCoverBtn) = createRefs()
            val coverImageModel = when (coverImageSource) {
                is ImageSource.Url -> (coverImageSource as ImageSource.Url).value
                is ImageSource.Local -> (coverImageSource as ImageSource.Local).value
                else -> null
            }
            AsyncImage(
                model = coverImageModel,
                contentDescription = "Cover Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4 / 1.5f)
                    .background(userGradient)
                    .constrainAs(cover) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                contentScale = ContentScale.Crop
            )
            Button(
                onClick = {
                    isSelectingProfileImage = false
                    photoPickerLauncher.launch(
                        ("image/*")
                    )
                },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .defaultMinSize(minHeight = 30.dp)
                    .constrainAs(updateCoverBtn) {
                        top.linkTo(parent.top, margin = 5.dp)
                        end.linkTo(parent.end, margin = 5.dp)
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = LocalExtraColors.current.textPrimary
                )
            ) {
                Text(
                    "Update Cover",
                    modifier = Modifier.padding(horizontal = 5.dp),
                    color = LocalExtraColors.current.textPrimary
                )
            }
            Column(
                modifier = Modifier
                    .constrainAs(profile) {
                        top.linkTo(cover.bottom, margin = (-60).dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImage(
                    size = 120,
                    userId = user._id,
                    profileImage = profileImageSource,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(100)
                        )
                )
                Button(
                    onClick = {
                        isSelectingProfileImage = true
                        photoPickerLauncher.launch(
                            ("image/*")
                        )
                    },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = LocalExtraColors.current.textPrimary
                    )
                ) {
                    Text(
                        text = "Update Profile Picture",
                        color = LocalExtraColors.current.textSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(100)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .constrainAs(form) {
                        top.linkTo(profile.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FormInputField(
                    modifier = Modifier.padding(10.dp),
                    label = "User Name",
                    value = formFields["userName"]!!,
                    onValueChange = { newValue ->
                        formFields["userName"] = newValue
                    },
                )
                FormInputField(
                    modifier = Modifier.padding(10.dp),
                    label = "Full Name",
                    value = formFields["fullName"] ?: "",
                    onValueChange = {
                        formFields["fullName"] = it
                    },
                )
                FormInputField(
                    modifier = Modifier.padding(10.dp),
                    label = "Bio",
                    value = formFields["bio"] ?: "",
                    onValueChange = {
                        formFields["bio"] = it
                    },
                )

                Button(
                    onClick = {
                        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

                        user.let {
                            if (it.userName != formFields["userName"]) {
                                requestBodyBuilder.addFormDataPart(
                                    "userName",
                                    formFields["userName"]!!
                                )
                            }
                            if (it.fullName != formFields["fullName"]) {
                                requestBodyBuilder.addFormDataPart(
                                    "fullName",
                                    formFields["fullName"]!!
                                )
                            }
                            if (it.bio != formFields["bio"]) {
                                requestBodyBuilder.addFormDataPart("bio", formFields["bio"]!!)
                            }
                            if (profileImageUri != null) {
                                val file = File(
                                    viewModel.getRealPathFromUri(
                                        context,
                                        profileImageUri
                                    )
                                )
                                val requestFile = file.asRequestBody("image/*".toMediaType())
                                requestBodyBuilder.addFormDataPart(
                                    "profileImage",
                                    file.name,
                                    requestFile
                                )
                            }
                            if (coverImageUri != null) {
                                val file =
                                    File(viewModel.getRealPathFromUri(context, coverImageUri))
                                val requestFile = file.asRequestBody("image/*".toMediaType())
                                requestBodyBuilder.addFormDataPart(
                                    "coverImage",
                                    file.name,
                                    requestFile
                                )
                            }
                        }

                        viewModel.updateProfile(requestBodyBuilder)
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = LocalExtraColors.current.textPrimary
                    ),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        if(isUpdatingProfile) "Updating..." else "Update", fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }

            }
        }

    }

}

