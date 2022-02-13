package com.tivasoft.biconui.ui.doctor.add_patient_schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.source.repositories.DoctorRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.DoctorIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddScheduleViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    val channel = Channel<DoctorIntent>(Channel.BUFFERED)

    private val _dataState =
        MutableStateFlow<DataState<Unit>>(DataState.Idle)
    val dataState: StateFlow<DataState<Unit>> get() = _dataState

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { doctorIntent ->
                when (doctorIntent) {
                    is DoctorIntent.CreateSchedule -> {
                        doctorRepository.createSchedule(doctorIntent.schedule)
                            .onEach { _dataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }
            }
        }
    }
}