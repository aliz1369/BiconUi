package com.tivasoft.biconui.data.model.network.responses.doctor

data class AddClinicResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<ClinicData>
)

data class ClinicData(
    val id: String,
    val name: String,
    val address: String,
    val location: LocationData,
    val phoneNumber: String,
    val attachment: ArrayList<String>
)

data class LocationData(
    val latitude: Double,
    val longitude: Double
)


