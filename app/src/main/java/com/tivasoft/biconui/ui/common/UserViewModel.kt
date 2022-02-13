package com.tivasoft.biconui.ui.common

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.model.network.responses.auth.DoctorResponse
import com.tivasoft.biconui.data.model.network.responses.auth.PatientResponse
import com.tivasoft.biconui.data.model.network.responses.auth.SearchDoctorResponse
import com.tivasoft.biconui.data.model.network.responses.auth.UserResponse
import com.tivasoft.biconui.data.source.repositories.AuthRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.AuthIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Contains User business logics.
 */
@OptIn(InternalCoroutinesApi::class)
@HiltViewModel
class UserViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : ViewModel() {
    val channel = Channel<AuthIntent>(Channel.BUFFERED)
    private val _dataState =
        MutableStateFlow<DataState<UserResponse>>(DataState.Idle)
    val dataState: StateFlow<DataState<UserResponse>> get() = _dataState

    private val _patientDataState =
        MutableStateFlow<DataState<PatientResponse>>(DataState.Idle)
    val patientDataState: StateFlow<DataState<PatientResponse>> get() = _patientDataState

    private val _doctorDataState =
        MutableStateFlow<DataState<DoctorResponse>>(DataState.Idle)
    val doctorDataState: StateFlow<DataState<DoctorResponse>> get() = _doctorDataState

    private val _searchDoctorForAssistanceDataState =
        MutableStateFlow<DataState<SearchDoctorResponse>>(DataState.Idle)
    val searchDoctorForAssistance: StateFlow<DataState<SearchDoctorResponse>> get() = _searchDoctorForAssistanceDataState

    init {
        handleIntent()
    }

    /**
     * if Create User  has been sent to the coroutine channel,
     * will start the Register process via AuthRepository and put the result in the dataState.
     */
    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { authIntent ->
                when (authIntent) {
                    is AuthIntent.UpdateUserIntent -> {
                        authRepository.updateUser(authIntent.userEntity)
                            .onEach { _dataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is AuthIntent.CreatePatient -> {
                        authRepository.createPatient(authIntent.patientEntity)
                            .onEach { _patientDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is AuthIntent.CreateDoctor -> {
                        authRepository.createDoctor(authIntent.doctorEntity)
                            .onEach { _doctorDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is AuthIntent.SearchDoctorForAssistanceIntent -> {
                        authRepository.searchDoctorForAssistance(authIntent.entity)
                            .onEach { _searchDoctorForAssistanceDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }
            }
        }
    }
}
