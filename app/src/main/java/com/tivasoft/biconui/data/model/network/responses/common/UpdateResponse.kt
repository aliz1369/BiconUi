package com.tivasoft.biconui.data.model.network.responses.common

data class UpdateResponse(
     val success : Boolean,
     val statusCode :Int,
     val data: UpdateData
)

data class UpdateData(
     val id: String,
     val fullName: String,
     val educationDegree: String,
     val educationField : String,
     val isAssistant : Boolean,
     val needAssistant : Boolean,
     val attachment : ArrayList<String>,
     val  grade: Int,
     val commissionRate: Double,
     val isConfirmedByAdmin : Boolean
)
