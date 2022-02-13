package com.tivasoft.biconui.data.model.local.ui

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleItem(
    val id: String,
    val doctorIcon: String = "",
    val patientIcon: String = "",
    @DrawableRes val sessionTypeIcon: Int = 0,
    val patientName: String = "",
    val doctorName: String = "",
    val sessionType: String = "",
    val time: String = "",
    val scheduleType: ScheduleType = ScheduleType.ITEM
) : Parcelable

enum class ScheduleType {
    ITEM,
    EMPTY,
    HEADER,
}