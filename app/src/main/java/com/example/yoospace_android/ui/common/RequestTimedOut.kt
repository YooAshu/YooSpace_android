package com.example.yoospace_android.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoospace_android.R
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun RequestTimedOut(
    onClick: () -> Unit,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.request_timed_out),
            contentDescription = "Request Timed Out",
            modifier = Modifier
                .fillMaxWidth(.5f)
                .padding(20.dp)
        )
        Text(
            "Request Timed Out",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Button(
            onClick = {
                onClick()
            },
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(text = "Retry", color = LocalExtraColors.current.textPrimary)
        }
    }
}