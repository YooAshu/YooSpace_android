package com.example.yoospace_android.ui.common

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.svg.SvgDecoder

@Composable
fun ProfileImage(
    userName: String,
    profileImage: String,
    size: Int,
    modifier: Modifier = Modifier

) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(SvgDecoder.Factory())
            add(OkHttpNetworkFetcherFactory())
        }
        .build()
    AsyncImage(
        profileImage.ifEmpty { "https://api.dicebear.com/9.x/big-smile/svg?seed=${userName}&backgroundColor=c0aede" },
        contentDescription = "Profile Image",
        imageLoader = imageLoader,
        onLoading = {
            Log.d("UserInfo", "Image loading started")
        },
        onSuccess = { success ->
            Log.d("UserInfo", "Image loaded successfully: ${success.result}")
        },
        onError = { error ->
            Log.e("UserInfo", "Image loading failed", error.result.throwable)
        },
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(100)),
        contentScale = ContentScale.Crop
    )
}