package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.model.network.requests.MeetRequest
import com.tivasoft.biconui.data.model.network.responses.common.MeetResponse
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


/**
 * Contains methods that will call on their respective APIs.
 */
class MeetRepository constructor(
    private val networkApi: NetworkApi
) {
    suspend fun call(meetRequest: MeetRequest): Flow<DataState<MeetResponse>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.call(meetRequest = meetRequest)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }

    suspend fun endCall(meetRequest: MeetRequest): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            val response = networkApi.endCall(meetRequest = meetRequest)
            emit(DataState.Success(response))
        } catch (exception: Exception) {
            emit(DataState.Failed(ErrorResponse(exception)))
        }
    }
}