package com.example.yoospace_android.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoospace_android.R
import com.example.yoospace_android.ui.auth.AuthViewModel
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
@Preview(showBackground = true)
fun Header(viewModel: AuthViewModel = viewModel(),){
    val error = viewModel.logoutError
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(LocalExtraColors.current.header)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(painter = painterResource(id = R.drawable.yoospace),
            contentDescription = "Yoospace Logo",
            modifier = Modifier
                .fillMaxWidth(.4f)


        )
        IconButton(
            onClick = {
                viewModel.logoutUser()
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = LocalExtraColors.current.btn1
            ),
            modifier = Modifier.
                padding(7.dp),
        ) {
            Image(painter = painterResource(id = R.drawable.logout),
                contentDescription = "Logout",
                modifier = Modifier.padding(5.dp).size(36.dp),
                colorFilter = ColorFilter.tint(LocalExtraColors.current.textPrimary)

            )
        }
        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
    }

}