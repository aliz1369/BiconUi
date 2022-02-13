package com.tivasoft.biconui.data.model.network.responses.assistant

data class GetDoctorDetailsResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: DoctorByAssistanceData
)

data class DoctorDetailsData(
    val id: String,
    val fullName: String,
    val educationDegree: String,
    val educationField: String,
    val hasAssistant: Boolean,
    val commissionRate: Double,
    val status: Int
)


