package com.tivasoft.biconui.ui.doctor.look_for_assistance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.model.network.responses.doctor.AssistanceResponse
import com.tivasoft.biconui.data.model.network.responses.doctor.GetAssistanceDetailsResponse
import com.tivasoft.biconui.data.source.repositories.DoctorRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.DoctorIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LookForAssistanceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val doctorRepository: DoctorRepository
) : ViewModel() {
    val channel = Channel<DoctorIntent>(Channel.UNLIMITED)
    private val _assistanceDataState =
        MutableStateFlow<DataState<AssistanceResponse>>(DataState.Idle)
    val assistanceDataState: StateFlow<DataState<AssistanceResponse>> get() = _assistanceDataState

    private val _assistanceDetailsDataState =
        MutableStateFlow<DataState<GetAssistanceDetailsResponse>>(DataState.Idle)
    val assistanceDetailsDataState: StateFlow<DataState<GetAssistanceDetailsResponse>> get() = _assistanceDetailsDataState

    private val _confirmDataState = MutableStateFlow<DataState<Unit>>(DataState.Idle)
    val confirmDataState: StateFlow<DataState<Unit>> get() = _confirmDataState

    private val _rejectAssistanceDataState =
        MutableStateFlow<DataState<Unit>>(DataState.Idle)
    val rejectAssistanceDataState: StateFlow<DataState<Unit>> get() = _rejectAssistanceDataState

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { doctorIntent ->
                when (doctorIntent) {
                    DoctorIntent.GetAllAssistance -> {
                        doctorRepository.getAllAssistance()
                            .onEach { _assistanceDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is DoctorIntent.ConfirmAssistance -> {
                        doctorRepository.confirmAssistance(doctorIntent.entity)
                            .onEach { _confirmDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is DoctorIntent.RejectAssistance -> {
                        doctorRepository.rejectAssistance(doctorIntent.id)
                            .onEach { _rejectAssistanceDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    DoctorIntent.GetAssistanceDetails -> {
                        doctorRepository.getAssistanceDetails()
                            .onEach { _assistanceDetailsDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }
            }
        }
    }
}