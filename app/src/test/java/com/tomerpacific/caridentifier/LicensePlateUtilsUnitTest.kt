package com.tomerpacific.caridentifier

import com.google.mlkit.vision.text.Text
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class LicensePlateUtilsUnitTest {
    private val validSevenDigitLicensePlateNumber = "17-655-76"
    private val anotherValidSevenDigitLicensePlateNumber = "12-345-67"
    private val validEightDigitLicensePlateNumber = "123-45-678"

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
    fun `should return correct license plate when it is eight digits`() {
        assert(isLicensePlateNumberValid(validEightDigitLicensePlateNumber))
    }

    @Test
    fun `should return correct license plate when it contains colons for eight digit license plate`() {
        val licensePlateWithColons = "123:45:678"
        val textBlocks = listOf(createTextBlock(licensePlateWithColons, 0.9f))
        val mockText = mock(Text::class.java)
        `when`(mockText.textBlocks).thenReturn(textBlocks)

        val licensePlate = getLicensePlateNumberFromImageText(mockText)
        assert(licensePlate == validEightDigitLicensePlateNumber)
    }

    @Test
    fun `should return correct license plate when it contains spaces for eight digit license plate`() {
        val licensePlateWithSpaces = "123 45 678"
        val textBlocks = listOf(createTextBlock(licensePlateWithSpaces, 0.9f))
        val mockText = mock(Text::class.java)
        `when`(mockText.textBlocks).thenReturn(textBlocks)

        val licensePlate = getLicensePlateNumberFromImageText(mockText)
        assert(licensePlate == validEightDigitLicensePlateNumber)
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

    private fun createTextBlock(
        text: String,
        confidence: Float = 0.0f,
    ): Text.TextBlock {
        val mockLine = mock(Text.Line::class.java)
        `when`(mockLine.text).thenReturn(text)
        `when`(mockLine.confidence).thenReturn(confidence)

        val mockBlock = mock(Text.TextBlock::class.java)
        `when`(mockBlock.lines).thenReturn(listOf(mockLine))
        return mockBlock
    }
}
