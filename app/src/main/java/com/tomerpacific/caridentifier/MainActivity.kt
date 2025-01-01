package com.tomerpacific.caridentifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.tomerpacific.caridentifier.model.MainViewModel
import com.tomerpacific.caridentifier.model.Screen

class MainActivity : ComponentActivity() {


    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            CreateNavigationGraph(navController)
        }
    }
    @Composable
    private fun CreateNavigationGraph(navController: NavHostController) {
        NavHost(navController, startDestination = Screen.MainScreen.route) {
            composable(route = Screen.MainScreen.route) {
                MainScreen(navController = navController)
            }
            dialog(route = Screen.LicensePlateNumberInput.route) { LicensePlateNumberDialog(navController, mainViewModel) }
            composable(route = Screen.CarDetailsScreen.route) { CarDetailsScreen(mainViewModel) }
            dialog(route = Screen.CameraPermission.route) { HandleCameraPermission(navController) }
        }
    }
}
