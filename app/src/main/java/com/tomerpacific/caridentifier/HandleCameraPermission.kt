package com.tomerpacific.caridentifier

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun HandleCameraPermission(navController: NavController,
                           mainViewModel: MainViewModel) {

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
            val activity = context.getActivityOrNull()
            activity?.let {
                if (shouldShowRequestPermissionRationale(it, android.Manifest.permission.CAMERA)) {
                    Toast.makeText(context, "Camera permission is needed to scan license plate", Toast.LENGTH_LONG).show()
                }
            }

            val cameraPermissionRequestLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                    cameraPermissionStatus = when(isGranted) {
                        true -> PackageManager.PERMISSION_GRANTED
                        false -> PackageManager.PERMISSION_DENIED
                    }

                mainViewModel.setDidRequestCameraPermission(true)
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

fun Context.getActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    return null
}