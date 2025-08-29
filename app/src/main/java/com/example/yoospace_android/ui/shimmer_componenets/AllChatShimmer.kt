package com.example.yoospace_android.ui.shimmer_componenets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.yoospace_android.R
import com.example.yoospace_android.utils.shimmerEffect

@Preview
@Composable
fun AllChatShimmer() {
    Column {
        Row {
            Box(
                Modifier
                    .width(70.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(100))
                    .shimmerEffect()
            )
            Spacer(Modifier.width(10.dp))
            Box(
                Modifier
                    .width(70.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(100))
                    .shimmerEffect()
            )
        }
        Column {
            ChatItemShimmer()
            ChatItemShimmer()
            ChatItemShimmer()
            ChatItemShimmer()
            ChatItemShimmer()
        }
    }
}

@Composable
fun ChatItemShimmer() {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(colorResource(R.color.photo_card_bg))
            .padding(5.dp)
    ) {
        Box(
            Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(100))
                .shimmerEffect()
        )
        Spacer(Modifier.width(10.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 5.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth(.3f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(100))
                    .shimmerEffect()
            )
            Spacer(Modifier.height(10.dp))

            Box(
                Modifier
                    .width(50.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(100))
                    .shimmerEffect()
            )


        }

    }
}