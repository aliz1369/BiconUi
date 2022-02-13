package com.tivasoft.biconui.data.model.network.responses.doctor

data class AllScheduleResponse(
    val success: Boolean,
    val statusCode: Int,
    val data: AllScheduleData
)

data class AllScheduleData(
    val today: ArrayList<Schedule>,
    val next: ArrayList<Schedule>
)

data class Schedule(
    val id: String,
    val connectionType: Int,
    val startTime: Long,
    val endTime: Long,
    val description: String,
    val patientName: String,
    val doctorName: String,
    val doctorPhoto: String? = "",
    val patientPhoto: String? = ""
)