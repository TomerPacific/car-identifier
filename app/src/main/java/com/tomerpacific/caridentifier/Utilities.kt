package com.tomerpacific.caridentifier

import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.CarReview

const val PROS = "יתרונות"
const val CONS = "חסרונות"
const val SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES = 9
const val EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES = 10


fun getCarManufacturer(manufacturer: String): String {
    return when (manufacturer) {
        "אאודי" -> "Audi"
        "אבארט" -> "Abarth"
        "אווטאר" -> "Avatar"
        "אוטוביאנקי" -> "Autobianchi"
        "איוויס" -> "Aiways"
        "אי.וי.איזי" -> "A.V.EZ"
        "אופל" -> "Opel"
        "אורה" -> "Ora"
        "איווקו" -> "Iveco"
        "אניאוס" -> "Ineos"
        "איסוזו" -> "Isuzu"
        "אינפיניטי" -> "Infiniti"
        "אלפא רומיאו" -> "Alfa Romeo"
        "אם.ג'י" -> "MG"
        "אסטון מרטין" -> "Aston Martin"
        "אל.אי.וי.סי" -> "L.E.V.C"
        "אל.טי.איי" -> "L.T.I"
        "אלפין" -> "Alpine"
        "אם דאבל יו אם" -> "M.W.M"
        "אקורה" -> "Acura"
        "אקס אי וי" -> "XEV"
        "אקספנג" -> "XPeng"
        "ב.מ.וו" -> "BMW"
        "בי.אי.דבאליו" -> "B.I.W"
        "ביואיק" -> "Buick"
        "בנטלי" -> "Bentley"
        "ג'אקו" -> "Jaecoo"
        "ג'י.איי.סי" -> "GAC"
        "ג'י.אם.סי" -> "GMC"
        "ג'ילי" -> "Geely"
        "ג'נסיס" -> "Genesis"
        "גופיל" -> "Goupil"
        "ג'יפ" -> "Jeep"
        "גיאיוואן" -> "Gyon"
        "גרייט וול" -> "Great Wall"
        "ג'יי.איי.סי" -> "JAC"
        "דאצ'יה" -> "Dacia"
        "דודג'" -> "Dodge"
        "האמר" -> "Hummer"
        "הונדה" -> "Honda"
        "וולוו" -> "Volvo"
        "טויוטה" -> "Toyota"
        "טסלה" -> "Tesla"
        "יגואר" -> "Jaguar"
        "יונדאי" -> "Hyundai"
        "לוטוס" -> "Lotus"
        "למבורגיני" -> "Lamborghini"
        "לנד רובר" -> "Land Rover"
        "לקסוס" -> "Lexus"
        "לינק אנד קו" -> "Lynk & Co"
        "לינקולן" -> "Lincoln"
        "מזדה" -> "Mazda"
        "מזראטי" -> "Maserati"
        "מיני" -> "Mini"
        "מיצובישי" -> "Mitsubishi"
        "מקלארן" -> "McLaren"
        "מרצדס" -> "Mercedes"
        "ניסאן" -> "Nissan"
        "סאאב" -> "Saab"
        "סאנגיונג" -> "Sangyong"
        "סובארו" -> "Subaru"
        "סוזוקי" -> "Suzuki"
        "סיטרואן" -> "Citroën"
        "סיאט" -> "Seat"
        "סקודה" -> "Skoda"
        "סרס" -> "Seres"
        "פיאט" -> "Fiat"
        "פיג'ו" -> "Peugeot"
        "פולסטאר" -> "Polestar"
        "פולקסווגן" -> "Volkswagen"
        "פורד" -> "Ford"
        "פורשה" -> "Porsche"
        "פרארי" -> "Ferrari"
        "קאדילק" -> "Cadillac"
        "קיה" -> "Kia"
        "קופרה" -> "Cupra"
        "קרייזלר" -> "Chrysler"
        "רנו" -> "Renault"
        "רולס רויס" -> "Rolls-Royce"
        "שברולט" -> "Chevrolet"
        "שיאופנג" -> "Xpeng"
        "צ'רי" -> "Chery"
        "וואי" -> "Wey"
        "זיקר" -> "Zeekr"
        "BYD" -> "BYD"
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
