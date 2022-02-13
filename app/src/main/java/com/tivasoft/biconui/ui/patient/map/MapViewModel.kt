package com.tivasoft.biconui.ui.patient.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.model.network.responses.patient.NearClinicData
import com.tivasoft.biconui.data.model.network.responses.patient.NearClinicResponse
import com.tivasoft.biconui.data.source.repositories.PatientRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.PatientIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val patientRepository: PatientRepository
) : ViewModel() {

    val channel = Channel<PatientIntent>(Channel.UNLIMITED)

    private val _nearClinicDataState =
        MutableStateFlow<DataState<NearClinicResponse>>(DataState.Idle)
    val nearClinicDataState: StateFlow<DataState<NearClinicResponse>> get() = _nearClinicDataState
    val clinics = arrayListOf<NearClinicData>()

    init {
        handleIntent()
    }


    private fun handleIntent() {

        viewModelScope.launch {
            channel.consumeAsFlow().collect { patientIntent ->
                when (patientIntent) {
                    is PatientIntent.GeNearClinics -> {
                        patientRepository.getNearClinic(patientIntent.entity)
                            .onEach {
                                _nearClinicDataState.value = it
                            }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }
            }
        }
    }
}