package com.tomerpacific.caridentifier.model

sealed class Screen(val route: String) {
    object MainScreen : Screen("main")
    object LicensePlateNumberInput : Screen("license_plate_number_input")
    object CarDetailsScreen : Screen("car_details_screen")
    object CameraPermission: Screen("camera_permission")
    object CameraPreview : Screen("camera_preview")
    object ImageOCR: Screen("image_ocr")
}
