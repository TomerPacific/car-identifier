package com.tomerpacific.caridentifier

import com.google.mlkit.vision.text.Text

const val SEVEN_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES = 9
const val EIGHT_DIGIT_LICENSE_NUMBER_LENGTH_WITH_DASHES = 10

private val licensePlateNumberPatterns =
    listOf(
        Regex("\\d{2}-\\d{3}-\\d{2}"),
        Regex("\\d{3}-\\d{2}-\\d{3}"),
    )

fun isLicensePlateNumberValid(licensePlateNumber: String): Boolean {
    return licensePlateNumberPatterns.any { pattern -> pattern.matches(licensePlateNumber) }
}

/**
 * Extracts a license plate number from the provided ML Kit Text object.
 *
 * This function sanitizes the text from each line by replacing colons and spaces with dashes.
 * It then finds all lines that match the predefined license plate patterns and returns the one
 * with the highest OCR confidence score.
 *
 * @param text The ML Kit Text object from which to extract the license plate number.
 * @return The license plate number with the highest confidence, or null if none is found.
 */
fun getLicensePlateNumberFromImageText(text: Text): String? {
    return text.textBlocks
        .flatMap { it.lines }
        .map { line ->
            Pair(line.text.replace(":", "-").replace(" ", "-"), line)
        }
        .filter { (sanitizedText, _) -> isLicensePlateNumberValid(sanitizedText) }
        .maxByOrNull { (_, line) -> line.confidence }?.first
}
