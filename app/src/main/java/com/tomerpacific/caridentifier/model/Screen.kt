package com.tomerpacific.caridentifier.model

import android.net.Uri

const val IMAGE_URI_KEY = "imageUri"

sealed class Screen(val route: String) {
    object MainScreen : Screen("main")

    object LicensePlateNumberInput : Screen("license_plate_number_input")

    object CarDetailsScreen : Screen("car_details_screen")

    object CameraPermission : Screen("camera_permission")

    object CameraPreview : Screen("camera_preview")

    object GalleryPicker : Screen("gallery_picker")

    object VerifyPhoto : Screen("verify_photo/{$IMAGE_URI_KEY}") {
        fun createRoute(uri: String) = "verify_photo/${Uri.encode(uri)}"
    }
}
