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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        onCaregiverClick = {
                            navController.navigate("caregiver")
                        },
                        onElderlyClick = {
                            navController.navigate("elderly")
                        }
                    )
                }

                composable("caregiver") {
                    CaregiverScreen()
                }

                composable("elderly") {
                    ElderlyScreen()
                }
            }
        }
    }
}