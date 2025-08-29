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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.yoospace_android.R
import com.example.yoospace_android.utils.shimmerEffect

@Preview
@Composable
fun CommentShimmer() {
    Column(
        Modifier
            .background(colorResource(R.color.photo_card_bg), shape = RoundedCornerShape(20.dp))
            .padding(10.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .shimmerEffect()
                )
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth(.3f)
                        .clip(RoundedCornerShape(20.dp))
                        .shimmerEffect()
                )
            }
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(20.dp))
                .shimmerEffect()
        )
        Box(
            Modifier
                .fillMaxWidth(.3f)
                .height(30.dp)
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(20.dp))
                .shimmerEffect()
        )
    }
}