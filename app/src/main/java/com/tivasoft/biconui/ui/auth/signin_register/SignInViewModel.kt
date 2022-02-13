package com.tivasoft.biconui.ui.auth.signin_register

import android.os.SystemClock
import androidx.lifecycle.*
import com.tivasoft.biconui.data.model.network.responses.auth.*
import com.tivasoft.biconui.data.source.repositories.AuthRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.AuthIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : ViewModel() {

    val channel = Channel<AuthIntent>(Channel.BUFFERED)

    private val _dataState =
        MutableStateFlow<DataState<PhoneLoginResponse>>(DataState.Idle)
    val dataState: StateFlow<DataState<PhoneLoginResponse>> get() = _dataState
    private val _phoneVerifyDataState =
        MutableStateFlow<DataState<VerifyCodeLoginResponse>>(DataState.Idle)
    val phoneVerifyDataState: StateFlow<DataState<VerifyCodeLoginResponse>> get() = _phoneVerifyDataState

    private val ONE_SECOND = 1000L
    private val mElapsedTime: MutableLiveData<Long> = MutableLiveData()
    private var mInitialTime: Long = 0
    private var timer: Timer? = null
    fun LiveDataTimerViewModel() {
        mInitialTime = SystemClock.elapsedRealtime()
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val newValue: Long = (SystemClock.elapsedRealtime() - mInitialTime) / 1000
                mElapsedTime.postValue(newValue)
            }
        }, ONE_SECOND, ONE_SECOND)
    }

    fun getElapsedTime(): LiveData<Long?>? {
        return mElapsedTime
    }

    init {
        handleIntent()
    }

    /**
     * if LoginIntent has been sent to the coroutine channel,
     * will start the login process via AuthRepository and put the result in the dataState.
     */
    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { authIntent ->
                when (authIntent) {
                    is AuthIntent.LoginIntent -> {
                        authRepository.login(authIntent.loginEntity)
                            .onEach { _dataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is AuthIntent.VerifyIntent -> {
                        val entity = authIntent.verifyEntity
                        authRepository.verify(entity)
                            .onEach { _phoneVerifyDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timer!!.cancel()
    }
}