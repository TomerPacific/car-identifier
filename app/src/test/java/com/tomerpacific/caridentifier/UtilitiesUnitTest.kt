package com.tomerpacific.caridentifier

import org.junit.Test

class UtilitiesUnitTest {

    private val carManufacturerNissan = "ניסאן"
    private val carManufacturerUnknown = "משהו"
    private val validLicensePlateNumber = "17-655-76"

    @Test
    fun `should return true if translated car manufacturer name is Nissan`() {
        val translatedCarManufacturer = getCarManufacturer(carManufacturerNissan)
        assert(translatedCarManufacturer == "Nissan")
    }

    @Test
    fun `should return true if translated car manufacturer name is Unknown`() {
        val translatedCarManufacturer = getCarManufacturer(carManufacturerUnknown)
        assert(translatedCarManufacturer == "Unknown Manufacturer")
    }

    @Test
    fun `should return true if license plate is valid`() {
        assert(isLicensePlateNumberValid(validLicensePlateNumber))
    }

    @Test
    fun `should return false if license plate is not valid`() {
        assert(!isLicensePlateNumberValid(""))
    }
}