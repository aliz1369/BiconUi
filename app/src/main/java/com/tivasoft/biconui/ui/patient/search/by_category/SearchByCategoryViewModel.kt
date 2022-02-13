package com.tivasoft.biconui.ui.patient.search.by_category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.model.network.responses.patient.BookResponse
import com.tivasoft.biconui.data.model.network.responses.auth.SearchDoctorResponse
import com.tivasoft.biconui.data.source.repositories.PatientRepository
import com.tivasoft.biconui.data.source.repositories.SearchRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.SearchIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchByCategoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val searchRepository: SearchRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {


    val channel = Channel<SearchIntent>(Channel.BUFFERED)

    private val _doctorsBySpecialtiesDataState =
        MutableStateFlow<DataState<SearchDoctorResponse>>(DataState.Idle)
    val doctorsBySpecialtiesDataState: StateFlow<DataState<SearchDoctorResponse>> get() = _doctorsBySpecialtiesDataState

    private val _bookDataState =
        MutableStateFlow<DataState<BookResponse>>(DataState.Idle)
    val bookDataState: StateFlow<DataState<BookResponse>> get() = _bookDataState

    init {
        handleIntent()

    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { searchIntent ->
                when (searchIntent) {
                    is SearchIntent.GetAllDoctorBySpecialties -> {
                        searchRepository.getDoctorsBySpecialties(searchIntent.id)
                            .onEach {
                                _doctorsBySpecialtiesDataState.value = it
                            }
                            .launchIn(viewModelScope)
                    }

                    is SearchIntent.Book -> {
                        patientRepository.book(searchIntent.bookEntity)
                            .onEach {
                                _bookDataState.value = it
                            }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }
            }


        }
    }


}
