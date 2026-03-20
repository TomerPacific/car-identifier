package com.tomerpacific.caridentifier

import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.CarReview

const val PROS_SECTION_HEBREW = "יתרונות"
const val CONS_SECTION_HEBREW = "חסרונות"
const val PROS_SECTION_ENGLISH = "Pros"
const val CONS_SECTION_ENGLISH = "Cons"
const val REVIEW_HEBREW = "ביקורת "
const val REVIEW_ENGLISH = " review"

fun formatCarReviewResponse(
    carReview: String,
    languageTranslator: LanguageTranslator,
): CarReview {
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

    val carReviewLines =
        carReview
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

private fun doesManufacturerNameExistInCommercialName(
    manufacturerName: String,
    commercialName: String,
): Boolean {
    return commercialName.contains(manufacturerName, ignoreCase = true)
}

fun handleErrorMessage(exception: Throwable): String {
    return exception.localizedMessage?.let {
        if (it.contains("[")) {
            it.substring(0, it.indexOf("[")).trim()
        } else {
            it.trim()
        }
    } ?: (exception.message ?: exception.toString())
}
