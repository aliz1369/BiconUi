package com.tivasoft.biconui.data.model.local.ui

import java.io.Serializable

class ScheduleEntity(
    var originId: Int,
    var patientName: String,
    var connectionType: ConnectionType,
    var scheduleDay: Int,
    var startTime: String,
    var endTime: String,
    var backgroundColor: String = "#dddddd",
    var textColor: String = "#ffffff",
    var year: Int,
    var month: Int,
    var dayOfMonth: Int,
    var description: String
) : Serializable

enum class ConnectionType(int: Int){
    VOICE_CALL(0),
    VIDEO_CALL(1),
    CHAT(2)
}