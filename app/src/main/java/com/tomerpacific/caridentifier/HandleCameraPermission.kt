package com.tomerpacific.caridentifier

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun HandleCameraPermission() {

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
                val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            cameraPermissionStatus = PackageManager.PERMISSION_DENIED
            when(isGranted) {
                true -> {

                }
                false -> {

                }

                }
            }
            LaunchedEffect(cameraPermissionStatus) {
                launcher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

}