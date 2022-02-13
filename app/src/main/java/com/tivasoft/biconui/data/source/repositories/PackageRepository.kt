package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.model.network.requests.doctor.CreatePackageRequest
import com.tivasoft.biconui.data.model.network.responses.doctor.CreatePackageResponse
import com.tivasoft.biconui.data.model.network.responses.doctor.GetItemResponse
import com.tivasoft.biconui.data.model.network.responses.common.PackageListResponse
import com.tivasoft.biconui.data.model.network.responses.doctor.PeriodResponse
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PackageRepository constructor(
    private val networkApi: NetworkApi
) {
    suspend fun getPatientPackageList(): Flow<DataState<PackageListResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getPatientPackageList()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getAdminPackageList(): Flow<DataState<PackageListResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAdminPackageList()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getDoctorPackageList(): Flow<DataState<PackageListResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getDoctorPackageList()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getPeriods(): Flow<DataState<PeriodResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getPeriods()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getItems(): Flow<DataState<GetItemResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getItems()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun createPackage(entity: CreatePackageRequest): Flow<DataState<CreatePackageResponse>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.createPackage(entity)
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }

    suspend fun changePackageActivation(packageId: String): Flow<Unit> = flow {
        try {
            networkApi.changePackageActivation(packageId)
        } catch (_: Exception) {
        }
    }

    suspend fun getDoctorPackagesById(doctorId: String): Flow<DataState<PackageListResponse>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.getDoctorPackagesById(doctorId)
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }
}