package com.tivasoft.biconui.ui.patient.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.model.network.responses.patient.BookResponse
import com.tivasoft.biconui.data.model.network.responses.doctor.SpecialtiesResponse
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
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val searchRepository: SearchRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {
    val channel = Channel<SearchIntent>(Channel.UNLIMITED)
    private val _searchAllDoctorsDataState =
        MutableStateFlow<DataState<SearchDoctorResponse>>(DataState.Idle)
    val searchAllDoctorsDataState: StateFlow<DataState<SearchDoctorResponse>> get() = _searchAllDoctorsDataState

    private val _allSpecialtiesDataState =
        MutableStateFlow<DataState<SpecialtiesResponse>>(DataState.Idle)
    val allSpecialtiesDataState: StateFlow<DataState<SpecialtiesResponse>> get() = _allSpecialtiesDataState

    private val _bookDataState =
        MutableStateFlow<DataState<BookResponse>>(DataState.Idle)


    init {
        handleIntent()

    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { searchIntent ->
                when (searchIntent) {
                    is SearchIntent.GetAllDoctor -> {
                        searchRepository.getAllDoctors(searchIntent.keyWord)
                            .onEach {
                                _searchAllDoctorsDataState.value = it
                            }
                            .launchIn(viewModelScope)
                    }

                    SearchIntent.GetAllSpecialties -> {
                        searchRepository.getAllSpecialties()
                            .onEach {
                                _allSpecialtiesDataState.value = it
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

                    is SearchIntent.GetFilteredDoctor -> {
                        searchRepository.getFilteredDoctors(searchIntent.filterModel)
                            .onEach {
                                _searchAllDoctorsDataState.value = it

                            }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }
            }
        }
    }


}