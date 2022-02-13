package com.tivasoft.biconui.ui.auth.signin_register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {
    var registerStatus = MutableLiveData<Boolean>(false)

    fun loading() {
            registerStatus.value = !registerStatus.value!!
    }
}