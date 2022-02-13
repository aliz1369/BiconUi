package com.tivasoft.biconui.ui.patient.tab_userprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.model.local.ui.ScheduleItem
import com.tivasoft.biconui.data.source.repositories.PatientRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.PatientIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchedulesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val patientRepository: PatientRepository
) : ViewModel() {
    val channel = Channel<PatientIntent>(Channel.BUFFERED)

    private val _allSchedulesDataState =
        MutableStateFlow<DataState<List<ScheduleItem>>>(DataState.Idle)
    val allSchedulesDataState: StateFlow<DataState<List<ScheduleItem>>> get() = _allSchedulesDataState

    var temp = ArrayList<ScheduleItem>()


    init {
        handleIntent()
    }


    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { patientIntent ->
                when (patientIntent) {
                    PatientIntent.GeAllSchedules -> {
                        patientRepository.getAllSchedules()
                            .onEach { _allSchedulesDataState.value = it }
                            .launchIn(viewModelScope)
                    }

                }
            }
        }
    }


}