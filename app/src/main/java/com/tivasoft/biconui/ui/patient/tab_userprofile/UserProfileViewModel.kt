package com.tivasoft.biconui.ui.patient.tab_userprofile

import androidx.lifecycle.*
import com.tivasoft.biconui.data.model.network.responses.common.GetMeResponse
import com.tivasoft.biconui.data.source.repositories.AuthRepository
import com.tivasoft.biconui.data.source.repositories.PushyRepository
import com.tivasoft.biconui.utils.channel_intents.AuthIntent
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.PatientIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val pushyRepository: PushyRepository
) : ViewModel() {
    val channel = Channel<AuthIntent>(Channel.BUFFERED)
    val patientChannel = Channel<PatientIntent>(Channel.BUFFERED)

    private val _dataState =
        MutableStateFlow<DataState<GetMeResponse>>(DataState.Idle)
    val dataState: StateFlow<DataState<GetMeResponse>> get() = _dataState

    init {
        handleIntent()
        handlePatientIntent() // todo correct package!!!
    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { authIntent ->
                when (authIntent) {
                    AuthIntent.GetMeIntent -> {
                        authRepository.getMe()
                            .onEach { _dataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun handlePatientIntent() {
        viewModelScope.launch {
            patientChannel.consumeAsFlow().collect { patientIntent ->
                when (patientIntent) {
                    is PatientIntent.RegisterPushy -> {
                        pushyRepository.registerPushyToken(patientIntent.entity)
                            .launchIn(viewModelScope)
                    }
                }
            }
        }
    }
}