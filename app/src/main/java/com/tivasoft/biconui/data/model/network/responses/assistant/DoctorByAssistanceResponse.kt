package com.tivasoft.biconui.data.model.network.responses.assistant

import com.google.gson.annotations.SerializedName

data class DoctorByAssistanceResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<DoctorByAssistanceData>
)

data class DoctorByAssistanceData(
    val id: String,
    val fullName: String,
    @SerializedName("isConfirmedByDoctor", alternate = ["hasDoctor"])
    val isConfirmedByDoctor: Boolean,
    @SerializedName("isConfirmedByAssistant", alternate = ["hasAssistant"])
    val isConfirmedByAssistant: Boolean,
    val educationField: String,
    val educationDegree: String,
    val status: Int
)
