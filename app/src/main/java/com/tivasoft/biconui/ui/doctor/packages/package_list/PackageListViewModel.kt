package com.tivasoft.biconui.ui.doctor.packages.package_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tivasoft.biconui.data.model.network.responses.common.PackageListResponse
import com.tivasoft.biconui.data.source.repositories.PackageRepository
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.channel_intents.PackageIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackageListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val packageRepository: PackageRepository
) : ViewModel() {

    val channel = Channel<PackageIntent>(Channel.BUFFERED)

    private val _packageDataState =
        MutableStateFlow<DataState<PackageListResponse>>(DataState.Idle)
    val packageDataState: StateFlow<DataState<PackageListResponse>> get() = _packageDataState

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collect { packageIntent ->
                when (packageIntent) {
                    PackageIntent.GetPatientPackages -> {
                        packageRepository.getPatientPackageList()
                            .onEach { _packageDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    PackageIntent.GetAdminPackages -> {
                        packageRepository.getAdminPackageList()
                            .onEach { _packageDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    PackageIntent.GetDoctorPackages -> {
                        packageRepository.getDoctorPackageList()
                            .onEach { _packageDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    is PackageIntent.ChangePackageActivation -> {
                        packageRepository.changePackageActivation(packageIntent.packageId)
                            .launchIn(viewModelScope)
                    }
                    is PackageIntent.GetDoctorPackagesById -> {
                        packageRepository.getDoctorPackagesById(packageIntent.doctorId)
                        .onEach { _packageDataState.value = it }
                            .launchIn(viewModelScope)
                    }
                    else -> Unit
                }
            }
        }
    }
}