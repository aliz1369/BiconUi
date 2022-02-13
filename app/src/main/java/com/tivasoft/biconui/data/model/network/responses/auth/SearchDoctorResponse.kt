package com.tivasoft.biconui.data.model.network.responses.auth

import javax.annotation.Nullable

data class SearchDoctorResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<SearchData>

)

data class SearchData(
    val id: String,
    val fullName: String,
    val educationDegree: String,
    @Nullable
    val photo: String?,
    val educationField: String,
    var isSelected: Boolean = false,
    var isFromLookForDoctors: Boolean = false,
    var isBooking: Boolean
)
