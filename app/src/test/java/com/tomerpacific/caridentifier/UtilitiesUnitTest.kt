package com.tomerpacific.caridentifier

import com.tomerpacific.caridentifier.model.CarDetails
import org.junit.Test

class UtilitiesUnitTest {
    private val carManufacturerNissan = "ניסאן"
    private val carManufacturerUnknown = "משהו"
    private val validLicensePlateNumber = "17-655-76"

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
    fun `should return true when license plate is valid`() {
        assert(isLicensePlateNumberValid(validLicensePlateNumber))
    }

    @Test
    fun `should return false when license plate is not valid`() {
        assert(!isLicensePlateNumberValid(""))
    }

    @Test
    fun `should return true when license plate is valid amd matches pattern`() {
        assert(isLicensePlateNumberValid(validLicensePlateNumber, Regex("^[0-9]{2,3}-[0-9]{2,3}-[0-9]{2,3}")))
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
