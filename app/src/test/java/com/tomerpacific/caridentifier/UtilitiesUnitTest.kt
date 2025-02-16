package com.tomerpacific.caridentifier

import org.junit.Test

class UtilitiesUnitTest {

    private val carManufacturerNissan = "ניסאן"
    private val carManufacturerUnknown = "משהו"
    private val validLicensePlateNumber = "17-655-76"

    @Test
    fun getCarManufacturer_correct() {
        val translatedCarManufacturer = getCarManufacturer(carManufacturerNissan)
        assert(translatedCarManufacturer == "Nissan")
    }

    @Test
    fun getCarManufacturer_unknown() {
        val translatedCarManufacturer = getCarManufacturer(carManufacturerUnknown)
        assert(translatedCarManufacturer == "Unknown Manufacturer")
    }

    @Test
    fun isLicensePlateValid_correct() {
        assert(isLicensePlateNumberValid(validLicensePlateNumber))
    }

    @Test
    fun isLicensePlateValid_incorrect() {
        assert(!isLicensePlateNumberValid(""))
    }
}