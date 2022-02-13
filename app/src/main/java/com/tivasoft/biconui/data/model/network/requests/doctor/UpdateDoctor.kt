package com.tivasoft.biconui.data.model.network.requests.doctor

import com.google.gson.annotations.SerializedName

data class UpdateDoctor(
    val email: String?,
    val commissionRate: Double,
    val educationDegree: String?,
    val specialty: String?,
    @SerializedName("class")
    val classType: String,
    val consulting: String,
    val needAssistant: Boolean,
    val photo: String
)