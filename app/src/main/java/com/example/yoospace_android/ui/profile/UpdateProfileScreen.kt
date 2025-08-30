package com.example.yoospace_android.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
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
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.navigation.ProtectedRoute
import com.example.yoospace_android.ui.common.FormInputField
import com.example.yoospace_android.ui.common.ImageSource
import com.example.yoospace_android.ui.common.ProfileImage
import com.example.yoospace_android.ui.theme.LocalExtraColors
import com.example.yoospace_android.utils.generateGradient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding

@Composable
fun UpdateProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController,
) {

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
                        activityBackgroundColor = android.graphics.Color.BLACK
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
            viewModel.toastMessage = null
        }
    }

    ProtectedRoute(navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Cover Image Section with Update Button
            Box {
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
                        .background(userGradient),
                    contentScale = ContentScale.Crop
                )

                // Update Cover Button positioned on top of cover image
                Button(
                    onClick = {
                        isSelectingProfileImage = false
                        photoPickerLauncher.launch("image/*")
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .defaultMinSize(minHeight = 30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = LocalExtraColors.current.textPrimary
                    )
                ) {
                    Text(
                        "Update Cover",
                        color = LocalExtraColors.current.textPrimary,
                        fontSize = 12.sp
                    )
                }
            }

            // Profile Image Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-60).dp)
                    .padding(horizontal = 20.dp),
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
                        photoPickerLauncher.launch("image/*")
                    },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 30.dp)
                        .padding(top = 8.dp),
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

            // Form Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .offset(y = (-60).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormInputField(
                    label = "User Name",
                    value = formFields["userName"]!!,
                    onValueChange = { newValue ->
                        formFields["userName"] = newValue
                    },
                )

                FormInputField(
                    label = "Full Name",
                    value = formFields["fullName"] ?: "",
                    onValueChange = {
                        formFields["fullName"] = it
                    },
                )

                FormInputField(
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
                        .fillMaxWidth(.5f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = LocalExtraColors.current.textPrimary
                    )
                ) {
                    Text(
                        if(isUpdatingProfile) "Updating..." else "Update",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}