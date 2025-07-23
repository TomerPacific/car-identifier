package com.tomerpacific.caridentifier.data.repository

class CarDetailsRepository(private val carLicensePlateSource: CarLicensePlateSource = CarLicensePlateSource()) {
    suspend fun getCarDetails(licensePlateNumber: String) = carLicensePlateSource.getCarDetails(licensePlateNumber)

    suspend fun getCarReview(searchQuery: String, locale: String) =
        carLicensePlateSource.getCarReview(searchQuery, locale)

}
