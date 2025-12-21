package com.tomerpacific.caridentifier.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.model.Screen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HandleGalleryPicker(navController: NavController) {
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    val encodedUri =
                        URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.name())
                    navController.navigate(Screen.VerifyPhoto.route + "/$encodedUri") {
                        // Pop the picker dialog so we go back directly to MainScreen from VerifyPhoto
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
