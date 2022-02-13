package com.tivasoft.biconui.ui.doctor.packages.add_package

import androidx.lifecycle.*
import com.tivasoft.biconui.data.model.local.ui.ItemEntity
import com.tivasoft.biconui.data.model.network.responses.doctor.CreatePackageResponse
import com.tivasoft.biconui.data.model.network.responses.doctor.GetItemResponse
import com.tivasoft.biconui.data.model.network.responses.doctor.PeriodResponse
import com.tivasoft.biconui.data.source.repositories.PackageRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.PackageIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPackageViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val packageRepository: PackageRepository
) : ViewModel() {
    val channel = Channel<PackageIntent>(Channel.BUFFERED)

    private val _periodDataState =
        MutableStateFlow<DataState<PeriodResponse>>(DataState.Idle)
    val periodDataState: StateFlow<DataState<PeriodResponse>> get() = _periodDataState

    private val _createPackageDataState =
        MutableStateFlow<DataState<CreatePackageResponse>>(DataState.Idle)
    val createPackageDataState: StateFlow<DataState<CreatePackageResponse>> get() = _createPackageDataState

    val items = MutableLiveData<List<ItemEntity>>()
    private val temp = ArrayList<ItemEntity>()

    private val _itemDataState =
        MutableStateFlow<DataState<GetItemResponse>>(DataState.Idle)
    val itemDataState: StateFlow<DataState<GetItemResponse>> get() = _itemDataState

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { packageIntent ->
                when (packageIntent) {
                    PackageIntent.GetPeriods -> {
                        packageRepository.getPeriods()
                            .onEach { _periodDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is PackageIntent.CreatePackage -> {
                        packageRepository.createPackage(packageIntent.entity)
                            .onEach { _createPackageDataState.value = it }
                            .launchIn(viewModelScope)
                    }

                    PackageIntent.GetItems -> {
                        packageRepository.getItems()
                            .onEach { _itemDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }
            }
        }
    }

    fun addItem(item: ItemEntity) {
        temp.addAll(listOf(item))
        items.value = temp
    }
}