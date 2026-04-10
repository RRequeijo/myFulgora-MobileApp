package com.example.myfulgora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfulgora.data.helpers.SettingsManager
import com.example.myfulgora.ui.screens.MainScreen
import com.example.myfulgora.ui.screens.SplashScreen
import com.example.myfulgora.ui.screens.auth.ForgotPasswordScreen
import com.example.myfulgora.ui.screens.auth.LoginScreen
import com.example.myfulgora.ui.screens.auth.OnboardingScreen
import com.example.myfulgora.ui.theme.MyFulgoraTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyFulgoraTheme {
                val navController = rememberNavController()

                val context = LocalContext.current
                val settingsManager = remember { SettingsManager(context) }
                val isOnboardingCompleted by settingsManager.isOnboardingCompletedFlow.collectAsState(initial = false)

                NavHost(navController = navController, startDestination = "splash") {

                    // 1. Splash
                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
                                val destino = if (isOnboardingCompleted) "login" else "onboarding"

                                navController.navigate(destino) {
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
                                navController.popBackStack()
                            },
                            onLoginAfterReset = {
                                navController.navigate("login") {
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