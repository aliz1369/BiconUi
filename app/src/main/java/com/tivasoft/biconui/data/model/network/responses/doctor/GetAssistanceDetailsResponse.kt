package com.tivasoft.biconui.data.model.network.responses.doctor

data class GetAssistanceDetailsResponse(
    val success: Boolean,
    val statusCode : Int,
    val data : AssistanceData
)
data class AssistanceDetails(
    val id : String,
    val fullName : String,
    val phoneNumber : String,
    val description : String,
    val attachment : ArrayList<String>,
    val hasDoctor : Boolean,

)
