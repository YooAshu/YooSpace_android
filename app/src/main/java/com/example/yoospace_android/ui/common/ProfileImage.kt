package com.example.yoospace_android.ui.common

import android.net.Uri
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
    userId: String,
    profileImage: ImageSource,
    size: Int,
    modifier: Modifier = Modifier
) {
    val model = when (profileImage) {
        is ImageSource.Url -> {
            profileImage.value.ifEmpty {
                "https://api.dicebear.com/9.x/big-smile/svg?seed=${userId}&backgroundColor=c0aede"
            }
        }
        is ImageSource.Local -> profileImage.value
        is ImageSource.LocalResource -> profileImage.value
    }

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(SvgDecoder.Factory())
            add(OkHttpNetworkFetcherFactory())
        }
        .build()

    AsyncImage(
        model = model,
        contentDescription = "Profile Image",
        imageLoader = imageLoader,
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(100)),
        contentScale = ContentScale.Crop
    )
}

sealed class ImageSource {
    data class Url(val value: String) : ImageSource()
    data class Local(val value: Uri) : ImageSource()
    data class LocalResource(val value: Int) : ImageSource()
}
