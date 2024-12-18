package com.tomerpacific.caridentifier.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CarDetails(
    @SerialName("license_plate_number")
    val licensePlateNumber: Int = 0,
    @SerialName("manufacture_country")
    val manufactureCountry: String = "",
    @SerialName("trim_level")
    val trimLevel: String = "",
    @SerialName("safety_feature_level")
    val safetyFeatureLevel: Int = 0,
    @SerialName("pollution_level")
    val pollutionLevel: Int = 0,
    @SerialName("year_of_production")
    val yearOfProduction: Int = 0,
    @SerialName("last_test_date")
    val lastTestDate: String = "",
    @SerialName("valid_date")
    val validDate: String = "",
    @SerialName("ownership")
    val ownership: String = "",
    @SerialName("frame_number")
    val frameNumber: String = "",
    @SerialName("color")
    val color: String = "",
    @SerialName("front_wheel")
    val frontWheel: String = "",
    @SerialName("rear_wheel")
    val rearWheel: String = "",
    @SerialName("fuel_type")
    val fuelType: String = "",
    @SerialName("first_on_road_date")
    val firstOnRoadDate: String = "",
    @SerialName("commercial_name")
    val commercialName: String = ""
)
