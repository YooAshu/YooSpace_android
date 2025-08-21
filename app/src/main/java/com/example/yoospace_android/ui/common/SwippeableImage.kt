package com.example.yoospace_android.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SwipeableImagePager(
    imageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { imageUrls.size }
    )


    Column(modifier = Modifier.fillMaxWidth()) {
        // Image pager
        HorizontalPager(
            state = pagerState,
            modifier = modifier
        ) { page ->
            AsyncImage(
                model = imageUrls[page],
                contentDescription = "Image $page",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp, horizontal = 5.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Dot indicators (if more than 1 image)
        if (imageUrls.size > 1) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                repeat(imageUrls.size) { index ->
                    val color = if (index == pagerState.currentPage) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    }

                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(6.dp)
                            .background(color, shape = RoundedCornerShape(100))
                    )
                }
            }
        }
    }
}
