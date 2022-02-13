package com.tivasoft.biconui.data.mapper

import com.tivasoft.biconui.data.model.local.ui.ScheduleItem
import com.tivasoft.biconui.data.model.network.responses.doctor.Schedule
import com.tivasoft.biconui.utils.EntityMapper
import com.tivasoft.biconui.R
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ScheduleMapper @Inject constructor() :
    EntityMapper<Schedule, ScheduleItem> {

    override fun mapFromEntity(entity: Schedule) = ScheduleItem(
        id = entity.id,
        doctorIcon = entity.doctorPhoto ?: "",
        patientIcon = entity.patientPhoto ?: "",
        patientName = entity.patientName,
        doctorName = entity.doctorName,
        sessionType = when (entity.connectionType) {
            1 -> "Video Call"
            2 -> "Voice Call"
            3 -> "Chat"
            else -> ""
        },
        sessionTypeIcon = when (entity.connectionType) {
            1 -> R.drawable.ic_video
            2 -> R.drawable.ic_microphone
            else -> R.drawable.ic_chat
        },
        time = getTimeFromTimeStamp(entity.startTime),
    )

    override fun mapFromEntityList(entities: List<Schedule>): List<ScheduleItem> =
        entities.map { mapFromEntity(it) }


    private fun getTimeFromTimeStamp(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.CHINA)
        return formatter.format(Date(timestamp))
    }

}