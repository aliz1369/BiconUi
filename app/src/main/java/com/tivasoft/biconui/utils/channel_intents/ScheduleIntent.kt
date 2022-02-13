package com.tivasoft.biconui.utils.channel_intents

sealed class ScheduleIntent {
    class GetSchedulesByDate(val date: Long) : ScheduleIntent()
    class GetScheduledDays(val date: Long) : ScheduleIntent()
    class GetScheduledMonths(val date: Long) : ScheduleIntent()
}