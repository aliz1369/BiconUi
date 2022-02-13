package com.tivasoft.biconui.ui.common.profile.add_schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.model.network.responses.doctor.GetAllPatientResponse
import com.tivasoft.biconui.data.source.repositories.DoctorRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.DoctorIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val doctorRepository: DoctorRepository
): ViewModel() {
        val channel = Channel<DoctorIntent>(Channel.BUFFERED)

    private val _allPatientDataState = MutableStateFlow<DataState<GetAllPatientResponse>>(DataState.Idle)
    val allPatientDataState: StateFlow<DataState<GetAllPatientResponse>> get() = _allPatientDataState

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { allPatient ->
                when(allPatient){
                    DoctorIntent.GetAllPatient -> {
                        doctorRepository.getAllPatient()
                            .onEach {
                                _allPatientDataState.value = it
                            }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }

            }
        }
    }


}