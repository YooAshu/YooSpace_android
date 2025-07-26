package com.example.yoospace_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.yoospace_android.data.local.TokenManager
import com.example.yoospace_android.navigation.AuthNavGraph
import com.example.yoospace_android.navigation.MainNavGraph
import com.example.yoospace_android.ui.components.BottomNav
import com.example.yoospace_android.ui.theme.YooSpace_androidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    modifier = Modifier.fillMaxSize(),
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YooSpace_androidTheme {
        Greeting("Android")
    }
}