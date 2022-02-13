package com.tivasoft.biconui.data.model.network.responses.auth

import javax.annotation.Nullable

data class DoctorResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: DoctorResponseData
)

data class DoctorResponseData(
    val id: String,
    val isAssistant: Boolean,
    @Nullable var attachment: ArrayList<String> = arrayListOf(),
    val isConfirmedByAdmin: Boolean,
    val assistant: AssistantResponseData
)

data class AssistantResponseData(
    val isConfirmedByDoctor: Boolean,
    @Nullable var attachment: ArrayList<String> = arrayListOf(),
)
//data class DoctorResponseData(
//    val assistant: AssistantInfo,
//    val isAssistant: Boolean,
//    @Nullable var attachment: ArrayList<String> = arrayListOf(),
//    val isConfirmedByAdmin: Boolean,
//    val _id: String,
//    val educationDegree: String,
//    val educationField: String,
//    val grade: Int,
//    val commissionRate: Int,
//    val user: String,
//    val createdAt: String,
//    val updatedAt: String,
//    val __v: Int
//)

data class AssistantInfo(
    @Nullable var attachment: ArrayList<String> = arrayListOf(),
    val isConfirmedByDoctor: Boolean,
    val phoneNumber: String,
    val description: String
)