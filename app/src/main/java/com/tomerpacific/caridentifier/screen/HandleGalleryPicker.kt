package com.tomerpacific.caridentifier.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.model.Screen

@Composable
fun handleGalleryPicker(navController: NavController) {
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    val encodedUri = Uri.encode(uri.toString())
                    navController.navigate(Screen.VerifyPhoto.route + "/$encodedUri") {
                        popUpTo(Screen.GalleryPicker.route) { inclusive = true }
                    }
                } else {
                    navController.popBackStack()
                }
            },
        )

    LaunchedEffect(Unit) {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
