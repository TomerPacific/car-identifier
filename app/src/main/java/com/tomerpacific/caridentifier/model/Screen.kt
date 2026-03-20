package com.tomerpacific.caridentifier.model

import android.net.Uri

private const val VERIFY_PHOTO_ARG = "imageUri"

sealed class Screen(val route: String) {
    object MainScreen : Screen("main")

    object LicensePlateNumberInput : Screen("license_plate_number_input")

    object CarDetailsScreen : Screen("car_details_screen")

    object CameraPermission : Screen("camera_permission")

    object CameraPreview : Screen("camera_preview")

    object GalleryPicker : Screen("gallery_picker")

    object VerifyPhoto : Screen("verify_photo/{$VERIFY_PHOTO_ARG}") {
        const val IMAGE_URI_KEY = VERIFY_PHOTO_ARG
        fun createRoute(uri: Uri) = route.replace("{$IMAGE_URI_KEY}", Uri.encode(uri.toString()))
    }
}
