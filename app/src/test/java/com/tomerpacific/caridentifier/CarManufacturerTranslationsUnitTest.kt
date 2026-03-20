package com.tomerpacific.caridentifier

import org.junit.Assert.assertEquals
import org.junit.Test

class CarManufacturerTranslationsUnitTest {
    private val carManufacturerNissan = "ניסאן"
    private val carManufacturerUnknown = "משהו"

    @Test
    fun `should return true when translated car manufacturer name is Nissan`() {
        val translatedCarManufacturer = getCarManufacturer(carManufacturerNissan)
        assertEquals("Nissan", translatedCarManufacturer)
    }

    @Test
    fun `should return true when translated car manufacturer name is Unknown`() {
        val translatedCarManufacturer = getCarManufacturer(carManufacturerUnknown)
        assertEquals("Unknown Manufacturer", translatedCarManufacturer)
    }
}
