package com.tomerpacific.caridentifier

import com.tomerpacific.caridentifier.model.CarDetails
import org.junit.Assert.assertEquals
import org.junit.Test

class CarStringUtilsUnitTest {

    @Test
    fun `should return true when car make and model are concatenated correctly`() {
        val carDetails =
            CarDetails(
                licensePlateNumber = 1765576,
                manufacturerCountry = "גרמניה",
                trimLevel = "SPORT",
                safetyFeatureLevel = 0,
                pollutionLevel = 15,
                yearOfProduction = 2013,
                lastTestDate = "2024-03-21",
                validDate = "2025-04-30",
                ownership = "פרטי",
                frameNumber = "WF0KXXGCBKDU75517",
                color = "כחול מטלי",
                frontWheel = "215/55R16",
                rearWheel = "215/55R16",
                fuelType = "בנזין",
                firstOnRoadDate = "2013-5",
                commercialName = "FOCUS",
                manufacturerName = "פורד",
                manufacturerNameEn = "Ford"
            )

        val concatenatedCarMakeAndModel = concatenateCarMakeAndModel(carDetails)
        assertEquals("Ford Focus Sport 2013", concatenatedCarMakeAndModel)
    }

    @Test
    fun `handleErrorMessage should truncate message containing square brackets`() {
        val exception = Exception("Network Error [404] Not Found")
        val result = handleErrorMessage(exception)
        assertEquals("Network Error", result)
    }

    @Test
    fun `handleErrorMessage should return full message when no square brackets present`() {
        val exception = Exception("Simple Error Message")
        val result = handleErrorMessage(exception)
        assertEquals("Simple Error Message", result)
    }

    @Test
    fun `handleErrorMessage should return exception string when localizedMessage and message are null`() {
        val exception = object : Exception() {
            override fun getLocalizedMessage(): String? = null
            override val message: String? = null
            override fun toString(): String = "CustomException"
        }
        val result = handleErrorMessage(exception)
        assertEquals("CustomException", result)
    }
}
