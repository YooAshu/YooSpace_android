package com.example.yoospace_android.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.yoospace_android.R
import com.example.yoospace_android.ui.common.FormInputField
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading = viewModel.isLoading
    val error = viewModel.loginError   // <-- don’t shadow with local var
    val response = viewModel.loginResponse
    val loginState = viewModel.loginState

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(loginState) {
        if (loginState is AuthViewModel.LoginState.Success) {
            navController.navigate("profile") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // Trigger dialog when error changes
    LaunchedEffect(error) {
        showDialog = error != null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .imePadding() // This handles keyboard padding
            .verticalScroll(rememberScrollState()) // Add scrolling
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.yoospace2),
            contentDescription = "Logo",
            modifier = Modifier.fillMaxWidth(.3f)
        )
        Image(
            painter = painterResource(id = R.drawable.yoospace_text),
            contentDescription = "Logo",
            modifier = Modifier.fillMaxWidth(.3f)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(LocalExtraColors.current.cardBackground)
                .border(0.dp, Color.Transparent, RoundedCornerShape(20.dp))
                .padding(16.dp),
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
                onClick = { viewModel.loginUser(email, password) },
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
            modifier = Modifier.clickable { onNavigateToRegister() }
        )

        // Error dialog
        if (showDialog && error != null) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(LocalExtraColors.current.cardBackground)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Error",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalExtraColors.current.textSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LocalExtraColors.current.btn1,
                            contentColor = LocalExtraColors.current.textPrimary
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        }

        // Success message
        if (response != null) {
            Text(
                "Welcome, ${response.data.user.userName}!",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

