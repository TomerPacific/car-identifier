package com.tomerpacific.caridentifier

import com.tomerpacific.caridentifier.model.CarDetails
import org.junit.Test

class UtilitiesUnitTest {
    private val carManufacturerNissan = "ניסאן"
    private val carManufacturerUnknown = "משהו"
    private val validSevenDigitLicensePlateNumber = "17-655-76"
    private val anotherValidSevenDigitLicensePlateNumber = "123-45-678"
    private val validEightDigitLicensePlateNumber = "123-456-78"

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
    fun `should return true when another seven digit license plate is valid`() {
        assert(isLicensePlateNumberValid(anotherValidSevenDigitLicensePlateNumber))
    }

    @Test
    fun `should return true when eight digit license plate is valid`() {
        assert(isLicensePlateNumberValid(validEightDigitLicensePlateNumber))
    }

    @Test
    fun `should return false when license plate is not valid`() {
        assert(!isLicensePlateNumberValid(""))
        assert(!isLicensePlateNumberValid("12-345-6789"))
        assert(!isLicensePlateNumberValid("1-23-45"))
        assert(!isLicensePlateNumberValid("12-ABC-78"))
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
}
