package com.example.yoospace_android.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yoospace_android.R
import com.example.yoospace_android.ui.components.FormInputField
import com.example.yoospace_android.ui.theme.ExtraColors
import com.example.yoospace_android.ui.theme.YooSpace_androidTheme
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToRegister: () -> Unit,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading = viewModel.isLoading
    val error = viewModel.loginError
    val response = viewModel.loginResponse

    val loginState = viewModel.loginState

    LaunchedEffect(loginState) {
        if (loginState is AuthViewModel.LoginState.Success) {
            navController.navigate("profile") {
                popUpTo("login") { inclusive = true } // clear login from backstack
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.yoospace),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(.5f)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)) // Clip to rounded corners
                .background(LocalExtraColors.current.cardBackground) // Apply background inside the clip
                .border(0.dp, Color.Transparent, RoundedCornerShape(20.dp))
                .padding(16.dp), // Add padding inside the rounded corners
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            FormInputField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
            )

            Spacer(modifier = Modifier.height(12.dp))

            FormInputField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.loginUser(email, password)
                },
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalExtraColors.current.btn1,
                    contentColor = LocalExtraColors.current.textPrimary
                ),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Logging in..." else "Login")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Don't have an account? Sign up here.",
            color = LocalExtraColors.current.textSecondary,
            modifier = Modifier.clickable {
                onNavigateToRegister()
            }
        )

        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        if (response != null) {
            Text(
                "Welcome, ${response.data.user.userName}!",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
