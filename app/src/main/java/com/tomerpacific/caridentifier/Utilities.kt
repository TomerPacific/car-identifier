package com.tomerpacific.caridentifier

import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.CarReview

const val PROS_SECTION_HEBREW = "יתרונות"
const val CONS_SECTION_HEBREW = "חסרונות"
const val PROS_SECTION_ENGLISH = "Pros"
const val CONS_SECTION_ENGLISH = "Cons"
const val REVIEW_HEBREW = "ביקורת "
const val REVIEW_ENGLISH = " review"
const val SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES = 9
const val EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES = 10

val CAR_MANUFACTURER_NAME_TRANSLATION_TO_ENGLISH = mapOf(
    "אאודי" to "Audi",
    "אבארט" to "Abarth",
    "אווטאר" to "Avatar",
    "אוטוביאנקי" to "Autobianchi",
    "איוויס" to "Aiways",
    "אי.וי.איזי" to "A.V.EZ",
    "אופל" to "Opel",
    "אורה" to "Ora",
    "איווקו" to "Iveco",
    "אניאוס" to "Ineos",
    "איסוזו" to "Isuzu",
    "אינפיניטי" to "Infiniti",
    "אלפא רומיאו" to "Alfa Romeo",
    "אם.ג'י" to "MG",
    "אסטון מרטין" to "Aston Martin",
    "אל.אי.וי.סי" to "L.E.V.C",
    "אל.טי.איי" to "L.T.I",
    "אלפין" to "Alpine",
    "אם דאבל יו אם" to "M.W.M",
    "אקורה" to "Acura",
    "אקס אי וי" to "XEV",
    "אקספנג" to "XPeng",
    "ב.מ.וו" to "BMW",
    "בי.אי.דבאליו" to "B.I.W",
    "ביואיק" to "Buick",
    "בנטלי" to "Bentley",
    "ג'אקו" to "Jaecoo",
    "ג'י.איי.סי" to "GAC",
    "ג'י.אם.סי" to "GMC",
    "ג'ילי" to "Geely",
    "ג'נסיס" to "Genesis",
    "גופיל" to "Goupil",
    "ג'יפ" to "Jeep",
    "גיאיוואן" to "Gyon",
    "גרייט וול" to "Great Wall",
    "ג'יי.איי.סי" to "JAC",
    "דאבל יו אם מוטורס" to "W.M. Motors",
    "דאצ'יה" to "Dacia",
    "דודג'" to "Dodge",
    "דונגפנג" to "Dongfeng",
    "די.אס" to "DS",
    "דייהו" to "Daewood",
    "דייהטסו" to "Daihatsu",
    "דיפאל" to "Deepal",
    "האמר" to "Hummer",
    "הונגצ'י" to "Hongqi",
    "הונדה" to "Honda",
    "וויה" to "Voyah",
    "ווי" to "Wey",
    "וולוו" to "Volvo",
    "זיקר" to "Zeekr",
    "טאטא" to "Tata",
    "טויוטה" to "Toyota",
    "טסלה" to "Tesla",
    "יגואר" to "Jaguar",
    "יונדאי" to "Hyundai",
    "יודו" to "Yudo",
    "לאדה" to "Lada",
    "לוטוס" to "Lotus",
    "לינק אנד קו" to "Lynk & Co",
    "למבורגיני" to "Lamborghini",
    "לנד רובר" to "Land Rover",
    "לקסוס" to "Lexus",
    "לינקולן" to "Lincoln",
    "ליפמוטור" to "Leafmotor",
    "לנצ'יה" to "Lancia",
    "מזדה" to "Mazda",
    "מאן" to "Man",
    "מורגן" to "Morgan",
    "מזראטי" to "Maserati",
    "מיני" to "Mini",
    "מיצובישי" to "Mitsubishi",
    "מקלארן" to "McLaren",
    "מקסוס" to "Maxus",
    "מרצדס" to "Mercedes",
    "נטע" to "Neta",
    "ניאו" to "Nio",
    "ניסאן" to "Nissan",
    "ננג'ינג" to "Nanjing",
    "סאאב" to "Saab",
    "סאנגיונג" to "Sangyong",
    "סאנשיין" to "Sunshine",
    "סובארו" to "Subaru",
    "סוזוקי" to "Suzuki",
    "סיטרואן" to "Citroën",
    "סיאט" to "Seat",
    "סקודה" to "Skoda",
    "סרס" to "Seres",
    "סמארט" to "Smart",
    "סנטרו" to "Santro",
    "סקיוול" to "Skywell",
    "פוטון" to "Foton",
    "פיאט" to "Fiat",
    "פיג'ו" to "Peugeot",
    "פולסטאר" to "Polestar",
    "פולקסווגן" to "Volkswagen",
    "פורד" to "Ford",
    "פורשה" to "Porsche",
    "פרארי" to "Ferrari",
    "פורתינג" to "Forthing",
    "פיאג'ו" to "Piaggio",
    "צ'רי" to "Chery",
    "קאדילק" to "Cadillac",
    "קארמה" to "Karma",
    "קיה" to "Kia",
    "קופרה" to "Cupra",
    "קרייזלר" to "Chrysler",
    "ראם" to "Ram",
    "רובר" to "Rover",
    "רנו" to "Renault",
    "ריהיי" to "Reyee",
    "רולס רויס" to "Rolls-Royce",
    "שברולט" to "Chevrolet"
)

fun getCarManufacturer(manufacturer: String): String {
    return CAR_MANUFACTURER_NAME_TRANSLATION_TO_ENGLISH[manufacturer] ?: "Unknown Manufacturer"
}

fun formatCarReviewResponse(carReview: String, languageTranslator: LanguageTranslator): CarReview {

    val prosSection: String
    val consSection: String

    when (languageTranslator.isHebrewLanguage()) {
        true -> {
            prosSection = PROS_SECTION_HEBREW
            consSection = CONS_SECTION_HEBREW
        }
        else -> {
            prosSection = PROS_SECTION_ENGLISH
            consSection = CONS_SECTION_ENGLISH
        }
    }

    val carReviewLines = carReview
        .removePrefix("\"")
        .removeSuffix("\"")
        .split("\\n")
    val prosList = mutableListOf<String>()
    val consList = mutableListOf<String>()
    var isInProsSection = false

    carReviewLines.forEach { line ->
        when {
            line.contains(prosSection, true) -> isInProsSection = true
            line.contains(consSection, true) -> isInProsSection = false
            line.isNotBlank() -> if (isInProsSection) prosList.add(line) else consList.add(line)
        }
    }

    return CarReview(prosList, consList)
}

fun concatenateCarMakeAndModel(carDetails: CarDetails): String {

    val manufacturerName = getCarManufacturer(carDetails.manufacturerName)
    var commercialName = carDetails.commercialName

    if (doesManufacturerNameExistInCommercialName(manufacturerName, commercialName)) {
        val indexOfManufacturerName = commercialName.indexOf(manufacturerName, ignoreCase = true)
        commercialName = commercialName.substring(indexOfManufacturerName + manufacturerName.length).trim()
    }

    return "$manufacturerName ${commercialName.lowercase().replaceFirstChar { it.titlecase() }
    } ${
        carDetails.trimLevel.lowercase().replaceFirstChar { it.titlecase() }
    } ${carDetails.yearOfProduction}"
}

fun isLicensePlateNumberValid(licensePlateNumber: String, pattern: Regex? = null): Boolean {
    return when (pattern) {
        null ->
            licensePlateNumber.length in SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES..
                    EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES &&
                    licensePlateNumber.contains("-")
        else ->
            licensePlateNumber.length in SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES..
                    EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES &&
                    pattern.matches(licensePlateNumber)
    }
}

private fun doesManufacturerNameExistInCommercialName(manufacturerName: String, commercialName: String): Boolean {
    return commercialName.contains(manufacturerName, ignoreCase = true)
}
