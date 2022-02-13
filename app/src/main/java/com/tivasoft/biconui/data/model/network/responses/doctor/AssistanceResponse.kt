package com.tivasoft.biconui.data.model.network.responses.doctor

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AssistanceResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<AssistanceData>
)

@Parcelize
data class AssistanceData(
    val id: String,
    val fullName: String,
    @SerializedName("isConfirmedByDoctor", alternate = ["hasDoctor"])
    val isConfirmedByDoctor: Boolean,
    val phoneNumber: String,
    val attachment: ArrayList<String>,
    val description: String,
    @SerializedName("isConfirmedByAssistant", alternate = ["hasAssistant"])
    val isConfirmedByAssistant: Boolean,
    val status: Int
) : Parcelable
