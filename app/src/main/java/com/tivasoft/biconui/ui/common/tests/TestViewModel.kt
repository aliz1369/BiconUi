package com.tivasoft.biconui.ui.common.tests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.model.network.responses.common.Backpack
import com.tivasoft.biconui.data.model.network.responses.tests.SingleTest
import com.tivasoft.biconui.data.model.network.responses.tests.TestResult
import com.tivasoft.biconui.data.source.repositories.TestRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.TestIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val testRepository: TestRepository
) : ViewModel() {
    val channel = Channel<TestIntent>(Channel.BUFFERED)

    private val _backPack = MutableStateFlow<DataState<List<Backpack>>>(DataState.Idle)
    val backpack: StateFlow<DataState<List<Backpack>>> get() = _backPack

    private val _testItem = MutableStateFlow<DataState<SingleTest>>(DataState.Idle)
    val testItem: StateFlow<DataState<SingleTest>> get() = _testItem

    private val _testResult = MutableStateFlow<DataState<TestResult>>(DataState.Idle)
    val testResult: StateFlow<DataState<TestResult>> get() = _testResult

    var currentIndex = 0
    val scoreList = ArrayList<Int>()

    init {
        scoreList.clear()
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is TestIntent.GetBackPack -> testRepository.getBackPack(intent.patientId)
                        .onEach { _backPack.value = it }
                        .launchIn(viewModelScope)
                    is TestIntent.GetSingleTest -> testRepository.getSingleTest(intent.testId)
                        .onEach { _testItem.value = it }
                        .launchIn(viewModelScope)
                    is TestIntent.SendTestResult -> testRepository.sendTestResult(intent.testResult)
                        .onEach { _testResult.value = it }
                        .launchIn(viewModelScope)
                }
            }
        }
    }

}