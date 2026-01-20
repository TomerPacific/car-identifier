package com.tomerpacific.caridentifier

import com.google.mlkit.vision.text.Text
import com.tomerpacific.caridentifier.model.CarDetails
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class UtilitiesUnitTest {
    private val carManufacturerNissan = "ניסאן"
    private val carManufacturerUnknown = "משהו"
    private val validSevenDigitLicensePlateNumber = "17-655-76"
    private val anotherValidSevenDigitLicensePlateNumber = "12-345-67"

    @Test
    fun `should return true when translated car manufacturer name is Nissan`() {
        val translatedCarManufacturer = getCarManufacturer(carManufacturerNissan)
        assert(translatedCarManufacturer == "Nissan")
    }

    @Test
    fun `should return true when translated car manufacturer name is Unknown`() {
        val translatedCarManufacturer = getCarManufacturer(carManufacturerUnknown)
        assert(translatedCarManufacturer == "Unknown Manufacturer")
    }

    @Test
    fun `should return true when seven digit license plate is valid`() {
        assert(isLicensePlateNumberValid(validSevenDigitLicensePlateNumber))
    }

    @Test
    fun `should return false when license plate is not valid`() {
        assert(!isLicensePlateNumberValid(""))
        assert(!isLicensePlateNumberValid("12-345-6789"))
        assert(!isLicensePlateNumberValid("1-23-45"))
        assert(!isLicensePlateNumberValid("12-ABC-78"))
    }

    @Test
    fun `should return correct license plate when it contains colons`() {
        val licensePlateWithColons = "12:345:67"
        val textBlocks = listOf(createTextBlock(licensePlateWithColons, 0.9f))
        val mockText = mock(Text::class.java)
        `when`(mockText.textBlocks).thenReturn(textBlocks)

        val licensePlate = getLicensePlateNumberFromImageText(mockText)
        assert(licensePlate == "12-345-67")
    }

    @Test
    fun `should return correct license plate when it contains spaces`() {
        val licensePlateWithSpaces = "12 345 67"
        val textBlocks = listOf(createTextBlock(licensePlateWithSpaces, 0.9f))
        val mockText = mock(Text::class.java)
        `when`(mockText.textBlocks).thenReturn(textBlocks)

        val licensePlate = getLicensePlateNumberFromImageText(mockText)
        assert(licensePlate == "12-345-67")
    }

    @Test
    fun `should return correct license plate when only one is present`() {
        val textBlocks =
            listOf(
                createTextBlock("some text", 0.5f),
                createTextBlock(validSevenDigitLicensePlateNumber, 0.9f),
            )
        val mockText = mock(Text::class.java)
        `when`(mockText.textBlocks).thenReturn(textBlocks)

        val licensePlate = getLicensePlateNumberFromImageText(mockText)
        assert(licensePlate == validSevenDigitLicensePlateNumber)
    }

    @Test
    fun `should return correct license plate when multiple are present`() {
        val textBlocks =
            listOf(
                createTextBlock(validSevenDigitLicensePlateNumber, 0.8f),
                createTextBlock(anotherValidSevenDigitLicensePlateNumber, 0.9f),
            )
        val mockText = mock(Text::class.java)
        `when`(mockText.textBlocks).thenReturn(textBlocks)

        val licensePlate = getLicensePlateNumberFromImageText(mockText)
        assert(licensePlate == anotherValidSevenDigitLicensePlateNumber)
    }

    @Test
    fun `should return license plate with highest confidence`() {
        val textBlocks =
            listOf(
                createTextBlock(validSevenDigitLicensePlateNumber, 0.9f),
                createTextBlock(anotherValidSevenDigitLicensePlateNumber, 0.8f),
            )
        val mockText = mock(Text::class.java)
        `when`(mockText.textBlocks).thenReturn(textBlocks)

        val licensePlate = getLicensePlateNumberFromImageText(mockText)
        assert(licensePlate == validSevenDigitLicensePlateNumber)
    }

    @Test
    fun `should return null when no license plate is present`() {
        val textBlocks =
            listOf(
                createTextBlock("some text", 0.5f),
                createTextBlock("some other text", 0.6f),
            )
        val mockText = mock(Text::class.java)
        `when`(mockText.textBlocks).thenReturn(textBlocks)

        val licensePlate = getLicensePlateNumberFromImageText(mockText)
        assert(licensePlate == null)
    }

    @Test
    fun `should return true when car make and model are concatenated correctly`() {
        val carDetails =
            CarDetails(
                1765576,
                "גרמניה",
                "SPORT",
                0,
                15,
                2013,
                "2024-03-21",
                "2025-04-30",
                "פרטי",
                "WF0KXXGCBKDU75517",
                "כחול מטלי",
                "215/55R16",
                "215/55R16",
                "בנזין",
                "2013-5",
                "FOCUS",
                "פורד",
            )

        val concatenatedCarMakeAndModel = concatenateCarMakeAndModel(carDetails)
        assert(concatenatedCarMakeAndModel == "Ford Focus Sport 2013")
    }

    private fun createTextBlock(
        text: String,
        confidence: Float?,
    ): Text.TextBlock {
        val mockLine = mock(Text.Line::class.java)
        `when`(mockLine.text).thenReturn(text)
        `when`(mockLine.confidence).thenReturn(confidence)

        val mockBlock = mock(Text.TextBlock::class.java)
        `when`(mockBlock.lines).thenReturn(listOf(mockLine))
        return mockBlock
    }
}
