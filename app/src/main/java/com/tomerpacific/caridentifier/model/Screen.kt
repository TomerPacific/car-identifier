package com.tomerpacific.caridentifier.model

sealed class Screen(val route: String) {
    object MainScreen : Screen("main")
    object LicensePlateNumberInput : Screen("license_plate_number_input")
}
