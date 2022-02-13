package com.tivasoft.biconui.data.model.network.responses.common

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName

data class Data(
    val id: String,
    val phoneNumber: String,
    val fullName: String,
    val accountType: Int,
    val createdAt: String,
    val updatedAt: String,
    val photo: String = "",
    val email: String,
    @Nullable val patient: GetMePatient?,
    @Nullable val doctor: GetMeDoctor?
)

data class GetMeResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: Data
)

data class GetMePatient(
    val id: String,
    val genderType: String,
    val age: String,
    @SerializedName("carrer")
    val career: String,
    val createdAt: String,
    val updatedAt: String,
    val photo: String = ""
)

data class GetMeDoctor(
    val id: String,
    val educationDegree: String,
    val educationField: String,
    val isAssistant: Boolean,
    val needAssistant: Boolean,
    val attachment: ArrayList<String> = arrayListOf(),
    val grade: Int,
    val commissionRate: Int,
    @SerializedName("class")
    val classField: String,
    val isConfirmedByAdmin: Boolean,
    val consulting: String,
    val photo: String = ""
)
