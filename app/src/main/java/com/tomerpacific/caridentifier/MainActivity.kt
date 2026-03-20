package com.tomerpacific.caridentifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tomerpacific.caridentifier.composable.CameraPreview
import com.tomerpacific.caridentifier.model.CarViewModel
import com.tomerpacific.caridentifier.model.IMAGE_URI_KEY
import com.tomerpacific.caridentifier.model.PermissionViewModel
import com.tomerpacific.caridentifier.model.Screen
import com.tomerpacific.caridentifier.screen.CarDetailsScreen
import com.tomerpacific.caridentifier.screen.HandleCameraPermission
import com.tomerpacific.caridentifier.screen.HandleGalleryPicker
import com.tomerpacific.caridentifier.screen.LicensePlateNumberDialog
import com.tomerpacific.caridentifier.screen.MainScreen
import com.tomerpacific.caridentifier.screen.VerifyPhotoDialog

class MainActivity : ComponentActivity() {

    private val permissionViewModel: PermissionViewModel by viewModels {
        PermissionViewModel.Factory(getPreferences(MODE_PRIVATE))
    }

    private val carViewModel: CarViewModel by viewModels {
        CarViewModel.Factory(applicationContext)
    }

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
                MainScreen(navController, permissionViewModel, carViewModel)
            }
            dialog(route = Screen.LicensePlateNumberInput.route) {
                LicensePlateNumberDialog(navController, carViewModel)
            }
            composable(route = Screen.CarDetailsScreen.route) {
                CarDetailsScreen(carViewModel, navController)
            }
            dialog(route = Screen.CameraPermission.route) {
                HandleCameraPermission(navController, permissionViewModel)
            }
            composable(route = Screen.CameraPreview.route) {
                CameraPreview(navController, carViewModel)
            }
            dialog(route = Screen.GalleryPicker.route) {
                HandleGalleryPicker(navController)
            }
            dialog(
                route = Screen.VerifyPhoto.route,
                arguments =
                    listOf(
                        navArgument(IMAGE_URI_KEY) {
                            type = NavType.StringType
                        },
                    ),
            ) {
                it.arguments?.let { bundle ->
                    bundle.getString(IMAGE_URI_KEY)?.let { uri ->
                        VerifyPhotoDialog(uri, navController, carViewModel)
                    }
                }
            }
        }
    }
}
