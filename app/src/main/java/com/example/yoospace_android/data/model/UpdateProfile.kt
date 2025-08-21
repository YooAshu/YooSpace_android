package com.example.yoospace_android.data.model

import java.io.File

data class UpdateProfileRequest(
    val userName: String,
    val fullName: String,
    val bio: String,
    val profileImage: File?,
    val coverImage: File?
)
