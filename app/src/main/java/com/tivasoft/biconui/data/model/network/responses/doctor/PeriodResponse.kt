package com.tivasoft.biconui.data.model.network.responses.doctor

data class PeriodResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: ArrayList<PeriodData>
)

data class PeriodData(
    val id: String,
    val title: String,
    val month: String

)
