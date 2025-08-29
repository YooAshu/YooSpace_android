package com.example.yoospace_android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.navigation.AuthNavGraph
import com.example.yoospace_android.navigation.MainNavGraph
import com.example.yoospace_android.ui.theme.YooSpace_androidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("MainActivity", "✅ Notification permission granted")
            } else {
                Log.w("MainActivity", "❌ Notification permission denied")
            }
        }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch from splash theme to app theme
        setTheme(R.style.Theme_YooSpace_android)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Ask for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        enableEdgeToEdge()
        setContent {
            YooSpace_androidTheme {
                val navController = rememberNavController()
                val isLoggedInState = remember { mutableStateOf(TokenManager.isLoggedIn.value) }

                LaunchedEffect(Unit) {
                    TokenManager.isLoggedIn.collect {
                        isLoggedInState.value = it
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize().imePadding(),

                ) { innerPadding ->
                    if (isLoggedInState.value) {
                        MainNavGraph(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )

                    } else {
                        AuthNavGraph(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
