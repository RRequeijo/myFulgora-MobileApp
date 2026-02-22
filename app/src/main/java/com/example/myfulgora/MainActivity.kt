package com.example.myfulgora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myfulgora.ui.theme.MyFulgoraTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfulgora.ui.screens.SplashScreen
import com.example.myfulgora.ui.screens.auth.OnboardingScreen
import com.example.myfulgora.ui.screens.auth.LoginScreen
import com.example.myfulgora.ui.screens.auth.ForgotPasswordScreen
import com.example.myfulgora.ui.screens.tabs.MainScreen
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyFulgoraTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {

                    // 1. Splash
                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
                                navController.navigate("onboarding") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. Onboarding
                    composable("onboarding") {
                        OnboardingScreen(
                            onFinish = {
                                navController.navigate("login") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 3. Login
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { navController.navigate("home") },
                            onForgotPasswordClick = { navController.navigate("forgot_password") }
                        )
                    }

                    // 4. Forgot Password
                    composable("forgot_password") {
                        ForgotPasswordScreen(
                            onNavigateBack = {
                                navController.popBackStack() // Volta para trás
                            },
                            onLoginAfterReset = {
                                navController.navigate("login") { // Vai para o Login
                                    popUpTo("forgot_password") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 5. Home (Dashboard Principal)
                    composable("home") {
                        MainScreen()
                    }
                }
            }
        }
    }
}