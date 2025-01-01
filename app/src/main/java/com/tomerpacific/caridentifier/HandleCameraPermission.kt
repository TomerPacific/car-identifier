package com.tomerpacific.caridentifier

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@Composable
fun HandleCameraPermission(navController: NavController) {

    val context = LocalContext.current

    var cameraPermissionStatus = remember {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        )
    }

    when (cameraPermissionStatus) {
        PackageManager.PERMISSION_GRANTED -> {

        }
        PackageManager.PERMISSION_DENIED -> {
            val cameraPermissionRequestLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                    cameraPermissionStatus = when(isGranted) {
                        true -> PackageManager.PERMISSION_GRANTED
                        false -> PackageManager.PERMISSION_DENIED
                    }
                    when(isGranted) {
                        true -> {
        
                        }
                        false -> {
                            navController.popBackStack()
                        }

                    }
            }

            LaunchedEffect(cameraPermissionStatus) {
                cameraPermissionRequestLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

}