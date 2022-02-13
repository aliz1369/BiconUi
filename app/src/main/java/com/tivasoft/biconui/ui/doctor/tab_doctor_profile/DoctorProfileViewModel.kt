package com.tivasoft.biconui.ui.doctor.tab_doctor_profile

import androidx.lifecycle.*
import com.tivasoft.biconui.data.model.local.ui.DayOfMoth
import com.tivasoft.biconui.data.model.local.ui.ScheduleItem
import com.tivasoft.biconui.data.model.local.ui.ScheduleType
import com.tivasoft.biconui.data.model.network.responses.common.ScheduledDays
import com.tivasoft.biconui.data.model.network.responses.common.ScheduledMonths
import com.tivasoft.biconui.data.source.repositories.ScheduleRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.ScheduleIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class DoctorProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    val channel = Channel<ScheduleIntent>(Channel.BUFFERED)
    private val _schedules = MutableStateFlow<DataState<ArrayList<ScheduleItem>>>(DataState.Idle)
    val schedules: StateFlow<DataState<ArrayList<ScheduleItem>>> get() = _schedules

    private val _daysOfMonth = MutableStateFlow<DataState<ScheduledDays>>(DataState.Idle)
    val daysOfMonth: StateFlow<DataState<ScheduledDays>> get() = _daysOfMonth

    private val _monthsOfYear = MutableStateFlow<DataState<ScheduledMonths>>(DataState.Idle)
    val monthsOfYear: StateFlow<DataState<ScheduledMonths>> get() = _monthsOfYear

    private val currentCalendar: Calendar by lazy { Calendar.getInstance() }
    val calendar: Calendar by lazy { Calendar.getInstance() }

    var lastTimeStamp: Long = System.currentTimeMillis()

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect {

                    scheduleIntent ->
                when (scheduleIntent) {
                    is ScheduleIntent.GetSchedulesByDate ->
                        scheduleRepository.getSchedulesByDate(scheduleIntent.date)
                            .onEach { _schedules.value = it }
                            .launchIn(viewModelScope)
                    is ScheduleIntent.GetScheduledDays ->
                        scheduleRepository.getScheduledDays(scheduleIntent.date)
                            .onEach { _daysOfMonth.value = it }
                            .launchIn(viewModelScope)
                    is ScheduleIntent.GetScheduledMonths ->
                        scheduleRepository.getScheduledMonths(scheduleIntent.date)
                            .onEach { _monthsOfYear.value = it }
                            .launchIn(viewModelScope)
                }
            }
        }
    }

    fun getDays(eventDays: ArrayList<Int>): ArrayList<DayOfMoth> {
        val days = ArrayList<DayOfMoth>()
        calendar.timeInMillis = lastTimeStamp
        for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            days.add(
                DayOfMoth(
                    day = i.toString(),
                    isToday = isCurrentMonth() && i == getToday(),
                    isSelected = isCurrentMonth() && i == getToday(),
                    hasEvent = eventDays.contains(i)
                )
            )
        return days
    }


    fun schedule(): DataState<ArrayList<ScheduleItem>> {
        var currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        var sch = ScheduleItem(
            id = "aaaa",
            doctorIcon = "",
            patientIcon = "111",
            sessionType = "1",
            sessionTypeIcon = 0,
            patientName = "aliz",
            doctorName = "Dr",
            time = currentDateTimeString,
            scheduleType = ScheduleType.ITEM
        )
        var schedul = ArrayList<ScheduleItem>()
        schedul.add(sch)
        return schedule()
    }

    fun getToday(): Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    fun isCurrentMonth() =
        currentCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)

}