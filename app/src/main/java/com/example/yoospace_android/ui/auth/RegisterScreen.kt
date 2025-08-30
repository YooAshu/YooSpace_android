package com.example.yoospace_android.ui.auth

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.yoospace_android.R
import com.example.yoospace_android.ui.common.FormInputField
import com.example.yoospace_android.ui.theme.LocalExtraColors

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }

    val isLoading = viewModel.isLoading
    val registerState = viewModel.registerState
    val context= LocalContext.current

    LaunchedEffect(registerState) {
        if (registerState is AuthViewModel.RegisterState.Success) {
            Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
            navController.navigate("login")
        }
    }

    val extraColors = LocalExtraColors.current

    // Use LazyColumn or Column with verticalScroll
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .imePadding() // This handles keyboard padding
            .verticalScroll(rememberScrollState()) // Add scrolling
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp) // Use spacedBy instead of manual Spacers
    ) {
        // Add some top padding to center content when keyboard is not visible
        Spacer(modifier = Modifier.weight(1f, fill = false))

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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(extraColors.cardBackground)
                .border(0.dp, Color.Transparent, RoundedCornerShape(20.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            FormInputField(
                value = userName,
                onValueChange = { userName = it },
                label = "User Name",
            )
            FormInputField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full Name",
            )
            FormInputField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
            )
            FormInputField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true
            )
            Button(
                onClick = {
                    viewModel.registerUser(email, password, userName, fullName)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalExtraColors.current.btn1,
                    contentColor = LocalExtraColors.current.textPrimary
                ),
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Registering..." else "Register")
            }
        }

        Text(
            text = "Already have an account? Login",
            color = LocalExtraColors.current.textSecondary,
            modifier = Modifier.clickable {
                onNavigateBack()
            }
        )

        // Add some bottom padding
        Spacer(modifier = Modifier.weight(1f, fill = false))
    }

    // Error dialog remains the same
    val error = viewModel.registerError
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        showDialog = error != null
    }

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
}