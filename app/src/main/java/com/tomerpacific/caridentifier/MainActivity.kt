package com.tomerpacific.caridentifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.tomerpacific.caridentifier.composable.CameraPreview
import com.tomerpacific.caridentifier.model.MainViewModel
import com.tomerpacific.caridentifier.model.Screen
import com.tomerpacific.caridentifier.screen.CarDetailsScreen
import com.tomerpacific.caridentifier.screen.HandleCameraPermission
import com.tomerpacific.caridentifier.screen.LicensePlateNumberDialog
import com.tomerpacific.caridentifier.screen.MainScreen

class MainActivity : ComponentActivity() {


    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getPreferences(MODE_PRIVATE)

        mainViewModel = MainViewModel(sharedPreferences)

        setContent {
            val navController = rememberNavController()
            CreateNavigationGraph(navController)
        }
    }
    @Composable
    private fun CreateNavigationGraph(navController: NavHostController) {
        NavHost(navController, startDestination = Screen.MainScreen.route) {
            composable(route = Screen.MainScreen.route) {
                MainScreen(navController, mainViewModel)
            }
            dialog(route = Screen.LicensePlateNumberInput.route) { LicensePlateNumberDialog(navController, mainViewModel) }
            composable(route = Screen.CarDetailsScreen.route) { CarDetailsScreen(mainViewModel) }
            dialog(route = Screen.CameraPermission.route) { HandleCameraPermission(navController, mainViewModel) }
            composable(route = Screen.CameraPreview.route) { CameraPreview(navController) }
        }
    }
}
