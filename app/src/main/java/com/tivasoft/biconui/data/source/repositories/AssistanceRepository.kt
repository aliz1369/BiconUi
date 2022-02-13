package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.model.network.requests.assistant.RejectDoctorByAssistanceRequest
import com.tivasoft.biconui.data.model.network.requests.assistant.BookDoctorRequest
import com.tivasoft.biconui.data.model.network.requests.assistant.BookDoctorsRequest
import com.tivasoft.biconui.data.model.network.responses.assistant.DoctorByAssistanceResponse
import com.tivasoft.biconui.data.model.network.responses.assistant.GetDoctorDetailsResponse
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AssistanceRepository constructor(
    private val networkApi: NetworkApi
) {
    suspend fun getAllDoctor(): Flow<DataState<DoctorByAssistanceResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAllDoctors()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun confirmDoctor(entity: BookDoctorRequest): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.confirmDoctor(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun rejectDoctor(entity: RejectDoctorByAssistanceRequest): Flow<DataState<Unit>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.rejectDoctor(entity.id)
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }

    suspend fun getDoctorDetails(): Flow<DataState<GetDoctorDetailsResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getDoctorDetails()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun sendRequestToDoctors(entity: BookDoctorsRequest): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.sendRequestToDoctors(entity)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }
}