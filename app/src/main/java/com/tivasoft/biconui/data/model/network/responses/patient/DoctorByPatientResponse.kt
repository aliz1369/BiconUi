package com.tivasoft.biconui.data.model.network.responses.patient

data class DoctorByPatientResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<DoctorData>
)

data class DoctorData(
    val id: String,
    val fullName: String,
    val createdAt: String,
    val updateAt: String,
    val photo: String? = ""
)