package com.example.yoospace_android.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PostTypeButtons(
    onMyPostsClick: () -> Unit, onLikedPostsClick: () -> Unit,
    typeOfPost: MutableIntState
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 30.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    typeOfPost.intValue = 0
                    onMyPostsClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "My Posts",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(10.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .padding(horizontal = 30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(if (typeOfPost.intValue == 0) 1f else 0.1f))
            )

        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    typeOfPost.intValue = 1
                    onLikedPostsClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Liked Posts",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(10.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .padding(horizontal = 30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(if (typeOfPost.intValue == 1) 1f else 0.1f))
            )

        }


    }
}
