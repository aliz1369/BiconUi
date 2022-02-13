package com.tivasoft.biconui.data.model.network.requests.doctor

data class ScheduleRequest(
     val connectionType : Int,
     val startTime : Long,
     val endTime :  Long,
     val description : String,
     val patient : String
)
