package com.paxtech.mobileapp.core.geocoding

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    @SerializedName("display_name")
    val displayName: String?,
    val address: Address?
)

data class Address(
    @SerializedName("road")
    val road: String?,
    @SerializedName("house_number")
    val houseNumber: String?,
    @SerializedName("suburb")
    val suburb: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("town")
    val town: String?,
    @SerializedName("village")
    val village: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("postcode")
    val postcode: String?
)

