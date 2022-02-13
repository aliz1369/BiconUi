package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.mapper.BackPackMapper
import com.tivasoft.biconui.data.model.network.responses.common.Backpack
import com.tivasoft.biconui.data.model.network.requests.test.TestResultRequest
import com.tivasoft.biconui.data.model.network.responses.tests.SingleTest
import com.tivasoft.biconui.data.model.network.responses.tests.TestResult
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestRepository constructor(
    private val networkApi: NetworkApi,
    private val backPackMapper: BackPackMapper
) {
    suspend fun getBackPack(patientId: String?): Flow<DataState<List<Backpack>>> = flow {
        emit(DataState.Loading)
        try {
            val response = if (patientId != null) {
                networkApi.getBackPack(patientId).data
            } else networkApi.getBackPack().data
            val mappedResponse = backPackMapper.mapFromEntityList(response)
            emit(DataState.Success(mappedResponse))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun getSingleTest(testId: String): Flow<DataState<SingleTest>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.getSingleTestById(testId)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun sendTestResult(testResult: TestResultRequest): Flow<DataState<TestResult>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.sendTestResult(testResult)
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }
}