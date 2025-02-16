package com.tomerpacific.caridentifier

import org.junit.Test

class UtilitiesUnitTest {

    private val carManufacturer = "ניסאן"
    @Test
    fun getCarManufacturer_correct() {
        val translatedCarManufacturer = getCarManufacturer(carManufacturer)
        assert(translatedCarManufacturer == "Nissan")
    }
}