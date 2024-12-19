package com.tomerpacific.caridentifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            CreateNavigationGraph(navController)
        }
    }
    @Composable
    private fun CreateNavigationGraph(navController: NavHostController) {
        NavHost(navController, startDestination = "main") {
            composable(route = "main") {
                MainScreen(navController = navController)
            }
            composable(route = "license_plate_number_input") { LicensePlateNumberDialog(navController)}
        }
    }
}
