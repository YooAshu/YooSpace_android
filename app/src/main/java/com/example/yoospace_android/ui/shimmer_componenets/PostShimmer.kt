package com.example.yoospace_android.ui.shimmer_componenets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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


@Composable
fun PostShimmer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .background(
                colorResource(R.color.photo_card_bg),
                RoundedCornerShape(35.dp)
            )
            .padding(5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(40.dp)
                    .padding(5.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                Modifier
                    .fillMaxWidth(.4f)
                    .height(30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shimmerEffect()
            )
        }
        Box(
            Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth()
                .height(15.dp)
                .clip(RoundedCornerShape(20.dp))
                .shimmerEffect()
        )

        Box(
            Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(35.dp))
                .aspectRatio(1f)
                .shimmerEffect()
        )
        Row {
            Box(Modifier
                .padding(top=10.dp)
                .height(35.dp)
                .fillMaxWidth(.3f)
                .clip(RoundedCornerShape(20.dp))
                .shimmerEffect())
            Spacer(Modifier.width(20.dp))
            Box(Modifier
                .padding(vertical =10.dp)
                .height(35.dp)
                .fillMaxWidth(.2f)
                .clip(RoundedCornerShape(20.dp))
                .shimmerEffect())
        }
    }
}