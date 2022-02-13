package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.model.network.requests.patient.FilterModel
import com.tivasoft.biconui.data.model.network.responses.doctor.SpecialtiesResponse
import com.tivasoft.biconui.data.model.network.responses.auth.SearchDoctorResponse
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepository constructor(
    private val networkApi: NetworkApi
) {
    suspend fun getAllDoctors(keyWord: String): Flow<DataState<SearchDoctorResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAllDoctors(keyWord)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getAllSpecialties(): Flow<DataState<SpecialtiesResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getAllSpecialties()
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getDoctorsBySpecialties(id: String): Flow<DataState<SearchDoctorResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getDoctorsBySpecialties(id)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getFilteredDoctors(filterModel: FilterModel): Flow<DataState<SearchDoctorResponse>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.getFilteredDoctors(
                    filterModel.genderType,
                    filterModel.specialties,
                    filterModel.consulting,
                    filterModel.clas,
                    filterModel.search
                )
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }
}