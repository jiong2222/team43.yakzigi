package com.example.yakzigi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yakzigi.screens.CaregiverScreen
import com.example.yakzigi.screens.ElderlyScreen
import com.example.yakzigi.screens.HomeScreen
import com.example.yakzigi.screens.LoginScreen
import com.example.yakzigi.screens.PairingScreen
import com.example.yakzigi.screens.RegisterScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("home") {
                                popUpTo("login") {
                                    inclusive = true
                                }
                            }
                        },
                        onRegisterClick = {
                            navController.navigate("register")
                        }
                    )
                }

                composable("register") {
                    RegisterScreen(
                        onRegisterSuccess = {
                            navController.navigate("home") {
                                popUpTo("register") {
                                    inclusive = true
                                }
                            }
                        },
                        onBackToLoginClick = {
                            navController.navigate("login")
                        }
                    )
                }

                composable("home") {
                    HomeScreen(
                        onCaregiverClick = {
                            navController.navigate("caregiver")
                        },
                        onElderlyClick = {
                            navController.navigate("elderly")
                        },
                        onPairingClick = {
                            navController.navigate("pairing")
                        }
                    )
                }

                composable("caregiver") {
                    CaregiverScreen()
                }

                composable("elderly") {
                    ElderlyScreen()
                }

                composable("pairing") {
                    PairingScreen()
                }
            }
        }
    }
}