package com.tivasoft.biconui.ui.common.profile.conversation_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.model.network.responses.common.ConversationData
import com.tivasoft.biconui.data.model.network.responses.common.ConversationResponse
import com.tivasoft.biconui.data.source.repositories.DoctorRepository
import com.tivasoft.biconui.data.source.repositories.PatientRepository
import com.tivasoft.biconui.utils.channel_intents.ConversationIntent
import com.tivasoft.biconui.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val doctorRepository: DoctorRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {
    val channel = Channel<ConversationIntent>(Channel.BUFFERED)

    private val _dataState = MutableStateFlow<DataState<ConversationResponse>>(DataState.Idle)
    val dataState: StateFlow<DataState<ConversationResponse>> get() = _dataState

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { conversationIntent ->
                when (conversationIntent) {
                    ConversationIntent.GetDoctorsByPatient -> {
                        patientRepository.getDoctorsByPatient()
                            .onEach { _dataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    ConversationIntent.GetAllPatientOfDoctor -> {
                        doctorRepository.getAllPatientOfDoctor()
                            .onEach { _dataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is ConversationIntent.ConfirmNewPatient -> {
                        doctorRepository.confirmPatient(conversationIntent.entity)
                            .onEach { _dataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is ConversationIntent.RejectPatient -> {
                        doctorRepository.rejectPatient(conversationIntent.entity)
                            .onEach { _dataState.value = it }
                            .launchIn(viewModelScope)
                    }
                }
            }
        }
    }

    /**
     * Group ConversationList based on value of PatientStatus.
     */
    fun sortByType(conversationList: ArrayList<ConversationData>): ArrayList<ConversationData> {
        val grouped = conversationList.filter { it.id.isNotBlank() }.groupBy { it.patientStatus }
        conversationList.clear()
        grouped.entries.map {
            conversationList.add(
                ConversationData(
                    id = "",
                    patientName = "",
                    patientIconUrl = "",
                    patientStatus = it.key,
                    symptom = "",
                    newMessageType = -1
                )
            )
            conversationList.addAll(it.value)
            conversationList.sortWith(
                compareBy({ item -> item.patientStatus },
                    { item -> item.newMessageType })
            )
        }
        return conversationList
    }
}