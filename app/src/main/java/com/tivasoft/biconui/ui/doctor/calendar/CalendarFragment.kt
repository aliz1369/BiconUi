package com.tivasoft.biconui.ui.doctor.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tivasoft.biconui.ui.doctor.tab_doctor_profile.DoctorProfileViewModel
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.ScheduleIntent
import com.events.calendar.views.EventsCalendar
import com.tivasoft.biconui.databinding.FragmentCalendarBinding
import com.tivasoft.biconui.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CalendarFragment : BaseFragment(), EventsCalendar.Callback {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DoctorProfileViewModel by activityViewModels()

    private var calendar = Calendar.getInstance()

    private var schedulesJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObserver()

        binding.apply {
            val today = Calendar.getInstance()
            val start = Calendar.getInstance()
            start.set(2020, 0, 1)
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 2)
            eventsCalendar.setSelectionMode(eventsCalendar.SINGLE_SELECTION)
                .setToday(today)
                .setMonthRange(start, end)
                .setWeekStartDay(Calendar.SUNDAY, false)
                .setIsBoldTextOnSelectionEnabled(true)
                .setCallback(this@CalendarFragment)
                .build()

            tvBack.setOnClickListener {
                findNavController().popBackStack()
            }
            showToday.setOnClickListener {
                eventsCalendar.goToToday(today, start)
                getData()
            }
        }
        getData()
    }

    private fun getData() {
        viewModel.lastTimeStamp = System.currentTimeMillis()
        calendar = Calendar.getInstance()
        getScheduledMonths(viewModel.lastTimeStamp)
        refreshScheduleData(viewModel.lastTimeStamp)
    }

    private fun getScheduledMonths(timeStamp: Long) {
        lifecycleScope.launch {
            viewModel.channel.send(ScheduleIntent.GetScheduledMonths(timeStamp))
        }
    }

    private fun refreshScheduleData(timeStamp: Long) {
        lifecycleScope.launch {
            viewModel.channel.apply {
                send(ScheduleIntent.GetSchedulesByDate(timeStamp))
                send(ScheduleIntent.GetScheduledDays(timeStamp))
            }
        }
    }

    private fun setEventDays(monthsOfYear: ArrayList<ArrayList<Int>>) {
        if (viewModel.lastTimeStamp != 0L)
            calendar.timeInMillis = viewModel.lastTimeStamp
        else
            calendar = Calendar.getInstance()
        binding.apply {
            monthsOfYear.mapIndexed { index, month ->
                month.map { day ->
                    calendar.set(Calendar.MONTH, index)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    eventsCalendar.addEvent(calendar)
                }
            }
            eventsCalendar.build()
        }
    }

    private fun subscribeObserver() {
        schedulesJob = lifecycleScope.launch {
            viewModel.monthsOfYear.collect { dataState ->
                when (dataState) {
                    is DataState.Idle -> Unit
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        setEventDays(dataState.data.data)
                    }
                    is DataState.Failed -> {
                        Toast.makeText(
                            requireContext(),
                            dataState.errorResponse.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("TAG", dataState.errorResponse.exception.toString())
                    }
                }
            }
        }
    }

    override fun onDayLongPressed(selectedDate: Calendar?) {
    }

    override fun onDaySelected(selectedDate: Calendar?) {
    }

    override fun onMonthChanged(monthStartDate: Calendar?) {
        monthStartDate?.let {
            viewModel.apply {
                if (lastTimeStamp != it.timeInMillis) {
                    lastTimeStamp = it.timeInMillis
                    refreshScheduleData(lastTimeStamp)
                    if (monthStartDate.get(Calendar.MONTH) == 0) {
                        getScheduledMonths(lastTimeStamp)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        schedulesJob?.cancel()
    }
}