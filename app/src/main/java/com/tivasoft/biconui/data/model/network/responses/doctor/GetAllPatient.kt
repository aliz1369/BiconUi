package com.tivasoft.biconui.data.model.network.responses.doctor

data class GetAllPatientResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<AllPatientData>
)

data class AllPatientData(
    val id: String,
    val patientName: String,
    val patientIconUrl: String? = ""
)


