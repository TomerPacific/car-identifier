package com.tomerpacific.caridentifier

import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.CarReview

const val PROS = "יתרונות"
const val CONS = "חסרונות"
const val SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES = 9
const val EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES = 10


fun getCarManufacturer(manufacturer: String): String {
    return when (manufacturer) {
        "סאאב" -> "Saab"
        "סאנגיונג" -> "Sangyong"
        "טויוטה" -> "Toyota"
        "מזדה" -> "Mazda"
        "יונדאי" -> "Hyundai"
        "קיה" -> "Kia"
        "ניסאן" -> "Nissan"
        "מיצובישי" -> "Mitsubishi"
        "פורד" -> "Ford"
        "שברולט" -> "Chevrolet"
        "הונדה" -> "Honda"
        "פולקסווגן" -> "Volkswagen"
        "סובארו" -> "Subaru"
        "סקודה" -> "Skoda"
        "רנו" -> "Renault"
        "פיג'ו" -> "Peugeot"
        "סיטרואן" -> "Citroën"
        "ב.מ.וו" -> "BMW"
        "מרצדס" -> "Mercedes"
        "אאודי" -> "Audi"
        "וולוו" -> "Volvo"
        "ג'יפ" -> "Jeep"
        "טסלה" -> "Tesla"
        "סוזוקי" -> "Suzuki"
        "פיאט" -> "Fiat"
        "אופל" -> "Opel"
        "סיאט" -> "Seat"
        "לקסוס" -> "Lexus"
        "אינפיניטי" -> "Infiniti"
        "לנד רובר" -> "Land Rover"
        "יגואר" -> "Jaguar"
        "דאצ'יה" -> "Dacia"
        "אם.ג'י" -> "MG"
        "פורשה" -> "Porsche"
        "מיני" -> "Mini"
        "אסטון מרטין" -> "Aston Martin"
        "רולס רויס" -> "Rolls-Royce"
        "בנטלי" -> "Bentley"
        "לוטוס" -> "Lotus"
        "מקלארן" -> "McLaren"
        "ביואיק" -> "Buick"
        "קאדילק" -> "Cadillac"
        "קרייזלר" -> "Chrysler"
        "ג'יפ" -> "Jeep"
        "דודג'" -> "Dodge"
        "האמר" -> "Hummer"
        "אלפא רומיאו" -> "Alfa Romeo"
        "מזראטי" -> "Maserati"
        "פרארי" -> "Ferrari"
        "למבורגיני" -> "Lamborghini"
        "קופרה" -> "Cupra"
        "פולסטאר" -> "Polestar"
        "BYD" -> "BYD"
        "שיאופנג" -> "Xpeng"
        "צ'רי" -> "Chery"
        "זיקר" -> "Zeekr"
        "סרס" -> "Seres"
        "וואי" -> "Wey"
        else -> "Unknown Manufacturer"
    }
}

fun formatCarReviewResponse(carReview: String): CarReview {
    val carReviewSplitIntoLines = carReview.removePrefix("\"").removeSuffix("\"").split("\\n")
    val prosList = mutableListOf<String>()
    val consList = mutableListOf<String>()
    var isInProsList = false
    carReviewSplitIntoLines.forEach { line ->
        if (line.isEmpty()) {
            return@forEach
        }

        if (line.contains(PROS, true)) {
            isInProsList = true
            return@forEach
        } else if (line.contains(CONS, true)) {
            isInProsList = false
            return@forEach
        }

        when (isInProsList) {
            true -> prosList.add(line)
            false -> consList.add(line)
        }
    }

    return CarReview(prosList, consList)
}

fun concatenateCarMakeAndModel(carDetails: CarDetails): String {
    return "${getCarManufacturer(carDetails.manufacturerName)} ${
        carDetails.commercialName.lowercase().replaceFirstChar { it.titlecase() }
    } ${
        carDetails.trimLevel.lowercase().replaceFirstChar { it.titlecase() }
    } ${carDetails.yearOfProduction}"
}

fun isLicensePlateNumberValid(licensePlateNumber: String, pattern: Regex? = null): Boolean {
    return when (pattern) {
        null -> {
            licensePlateNumber.length in SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES..EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES && licensePlateNumber.contains("-")
        }
        else -> {
            licensePlateNumber.length in SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES..EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES && pattern.matches(licensePlateNumber)
        }
    }
}
