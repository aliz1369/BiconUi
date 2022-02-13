package com.tivasoft.biconui.ui.doctor.tab_prescriptions

import androidx.lifecycle.*
import com.tivasoft.biconui.data.model.local.ui.Prescription
import com.tivasoft.biconui.data.model.network.responses.common.PrescriptionResponse
import com.tivasoft.biconui.data.model.network.responses.tests.SingleTest
import com.tivasoft.biconui.data.source.repositories.PatientRepository
import com.tivasoft.biconui.data.source.repositories.TestRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.SearchIntent
import com.tivasoft.biconui.utils.channel_intents.TestIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrescriptionsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val patientRepository: PatientRepository,
    private val testRepository: TestRepository
    ) : ViewModel() {
    val channel = Channel<SearchIntent>(Channel.UNLIMITED)
    val testsChannel = Channel<TestIntent>(Channel.BUFFERED)

    private val _prescriptions = MutableLiveData<ArrayList<Prescription>>()
    val prescriptions: LiveData<ArrayList<Prescription>> get() = _prescriptions
    private val temp = ArrayList<Prescription>()

    private val _allPrescriptionDataState =
        MutableStateFlow<DataState<PrescriptionResponse>>(DataState.Idle)
    val allPrescriptionDataState: StateFlow<DataState<PrescriptionResponse>> get() = _allPrescriptionDataState

    private val _testItem = MutableStateFlow<DataState<SingleTest>>(DataState.Idle)
    val testItem: StateFlow<DataState<SingleTest>> get() = _testItem

    init {
        handleIntent()
        handleTestsIntent()
    }

    private fun handleIntent(){
        viewModelScope.launch {
            channel.consumeAsFlow().collect { searchIntent ->
                when(searchIntent){
                    SearchIntent.GetAllPrescription -> {
                        patientRepository.getAllPrescription()
                            .onEach {
                                _allPrescriptionDataState.value = it
                            }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun handleTestsIntent() {
        viewModelScope.launch {
            testsChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is TestIntent.GetSingleTest -> testRepository.getSingleTest(intent.testId)
                        .onEach { _testItem.value = it }
                        .launchIn(viewModelScope)
                    is TestIntent.SendTestResult -> Unit
                }
            }
        }
    }
}