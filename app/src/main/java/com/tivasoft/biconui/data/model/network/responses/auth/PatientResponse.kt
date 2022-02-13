package com.tivasoft.biconui.data.model.network.responses.auth

import com.google.gson.annotations.SerializedName

data class PatientResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: PatientData
)

data class PatientData(
    val id: String,
    val genderType: String,
    val age: String,
    @SerializedName("carrer")
    var career: String,
    val createdAt: String,
    val updatedAt: String,
)