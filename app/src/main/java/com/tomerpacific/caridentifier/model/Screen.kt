package com.tomerpacific.caridentifier.model

import android.net.Uri

sealed class Screen(route: String) {
    open val route: String = route

    object MainScreen : Screen("main")

    object LicensePlateNumberInput : Screen("license_plate_number_input")

    object CarDetailsScreen : Screen("car_details_screen")

    object CameraPermission : Screen("camera_permission")

    object CameraPreview : Screen("camera_preview")

    object GalleryPicker : Screen("gallery_picker")

    object VerifyPhoto : Screen("") {
        const val IMAGE_URI_KEY = "imageUri"
        private const val BASE_ROUTE = "verify_photo"
        override val route = "$BASE_ROUTE/{$IMAGE_URI_KEY}"
        fun createRoute(uri: Uri) = "$BASE_ROUTE/${Uri.encode(uri.toString())}"
    }
}
