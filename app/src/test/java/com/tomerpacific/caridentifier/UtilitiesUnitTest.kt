package com.tomerpacific.caridentifier

import org.junit.Test

class UtilitiesUnitTest {

    private val carManufacturerNissan = "ניסאן"
    private val carManufacturerUnknown = "משהו"


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
}