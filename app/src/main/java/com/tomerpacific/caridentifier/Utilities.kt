package com.tomerpacific.caridentifier

fun getCarManufacturer(manufacturer: String): String {
    return when (manufacturer) {
        "טויוטה" -> "Toyota"
        "סובארו" -> "Subaru"
        "סוזוקי" -> "Suzuki"
        "סיאט" -> "Seat"
        "סאאב" -> "Saab"
        "סאנגיונג" -> "Sangyong"
        "פורד" -> "Ford"
        "פיאט" -> "Fiat"
        "פולקסווגן" -> "Volkswagen"
        "פיג'ו" -> "Peugeot"
        "פונטיאק" -> "Pontiac"
        "פורשה" -> "Porsche"
        else -> "Unknown Manufacturer"
    }
}
