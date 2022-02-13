package com.tivasoft.biconui.ui.assistant.look_for_doctor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.model.network.responses.assistant.DoctorByAssistanceResponse
import com.tivasoft.biconui.data.model.network.responses.assistant.GetDoctorDetailsResponse
import com.tivasoft.biconui.data.source.repositories.AssistanceRepository
import com.tivasoft.biconui.utils.channel_intents.AssistanceIntent
import com.tivasoft.biconui.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LookForDoctorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val assistanceRepository: AssistanceRepository
) : ViewModel() {
    val channel = Channel<AssistanceIntent>(Channel.UNLIMITED)

    private val _doctorDataState =
        MutableStateFlow<DataState<DoctorByAssistanceResponse>>(DataState.Idle)
    val doctorDataState: StateFlow<DataState<DoctorByAssistanceResponse>> get() = _doctorDataState

    private val _doctorDetailsDataState =
        MutableStateFlow<DataState<GetDoctorDetailsResponse>>(DataState.Idle)
    val doctorDetailsDataState: StateFlow<DataState<GetDoctorDetailsResponse>> get() = _doctorDetailsDataState

    private val _confirmDataState = MutableStateFlow<DataState<Unit>>(DataState.Idle)
    val confirmDataState: StateFlow<DataState<Unit>> get() = _confirmDataState

    private val _rejectAssistanceDataState =
        MutableStateFlow<DataState<Unit>>(DataState.Idle)
    val rejectAssistanceDataState: StateFlow<DataState<Unit>> get() = _rejectAssistanceDataState

    private val _requests = MutableStateFlow<DataState<Unit>>(DataState.Idle)
    val requests: StateFlow<DataState<Unit>> get() = _requests

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { assistanceIntent ->
                when (assistanceIntent) {
                    AssistanceIntent.GetDoctors -> {
                        assistanceRepository.getAllDoctor()
                            .onEach { _doctorDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is AssistanceIntent.ConfirmDoctor -> {
                        assistanceRepository.confirmDoctor(assistanceIntent.entity)
                            .onEach { _confirmDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is AssistanceIntent.RejectDoctor -> {
                        assistanceRepository.rejectDoctor(assistanceIntent.id)
                            .onEach { _rejectAssistanceDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    AssistanceIntent.GetDoctorDetails -> {
                        assistanceRepository.getDoctorDetails()
                            .onEach { _doctorDetailsDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is AssistanceIntent.SubmitRequest -> {
                        assistanceRepository.sendRequestToDoctors(assistanceIntent.entity)
                            .onEach { _requests.value = it }
                            .launchIn(viewModelScope)
                    }
                }
            }
        }
    }
}