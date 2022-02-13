package com.tivasoft.biconui.data.model.network.responses.common

data class ScheduleResponse(
    val success: Boolean,
    val statusCode : Int,
    val data : ArrayList<ScheduleData>
)

data class ScheduleData(
    val connectionType : Int,
    val startTime : Long,
    val endTime :  Long,
    val description : String,
    val patient : String
)
