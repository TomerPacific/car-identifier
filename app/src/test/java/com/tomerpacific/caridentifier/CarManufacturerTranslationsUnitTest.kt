package com.tomerpacific.caridentifier

import org.junit.Test

class CarManufacturerTranslationsUnitTest {
    private val carManufacturerNissan = "ניסאן"
    private val carManufacturerUnknown = "משהו"

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
}
