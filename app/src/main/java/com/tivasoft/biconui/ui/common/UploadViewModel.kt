package com.tivasoft.biconui.ui.common

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.source.repositories.UploadRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.MainIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * Contains Upload business logics.
 */
@HiltViewModel
class UploadViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val uploadRepository: UploadRepository
) : ViewModel() {
    val channel = Channel<MainIntent>(Channel.BUFFERED)

    private val _dataState = MutableStateFlow<DataState<List<String>>>(DataState.Idle)
    val dataState: StateFlow<DataState<List<String>>> get() = _dataState

    val imageUriList = ArrayList<Uri>()
    val multiPartList = ArrayList<MultipartBody.Part>()

    init {
        handleIntent()
    }
    /**
     * if MainIntent  has been sent to the coroutine channel,
     * will start the Register process via AuthRepository and put the result in the dataState.
     */
    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { mainIntent ->
                when (mainIntent) {
                    MainIntent.UploadFiles -> {
                        uploadRepository.uploadFiles(multiPartList)
                            .onEach { _dataState.value = it }
                            .launchIn(viewModelScope)
                    }
                }
            }
        }
    }
}