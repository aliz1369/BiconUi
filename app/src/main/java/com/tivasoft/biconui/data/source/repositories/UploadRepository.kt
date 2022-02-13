package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.model.network.responses.common.SocketFilesResponse
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.utils.DataState
import com.tivasoft.biconui.utils.ErrorResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Contains methods that will call on their respective APIs.
 */
class UploadRepository constructor(
    private val networkApi: NetworkApi
) {
    /**
     * Send a Login entity to login API and emit the result for each state.
     *
     * @param file the File  that we send to API
     *
     * @return API response in form of a coroutine flow
     */
    private suspend fun uploadFile(file: MultipartBody.Part): Flow<String> = flow {
        try {
            val response = networkApi.uploadFile(file)
            emit(response.data)
        } catch (_: Exception) {
        }
    }

    /**
     * Send a Login entity to login API and emit the result for each state.
     *
     * @param files the Files  that we send to API
     *
     * @return API response in form of a coroutine flow
     */
    suspend fun uploadFiles(files: ArrayList<MultipartBody.Part>): Flow<DataState<List<String>>> =
        flow {
            emit(DataState.Loading)
            try {
                coroutineScope {
                    val fileList = files.map {
                        async { uploadFile(it).first() }
                    }.awaitAll()
                    emit(DataState.Success(fileList))
                }
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }

    suspend fun uploadChatFile(requestBody: RequestBody): Flow<DataState<SocketFilesResponse>> =
        flow {
            emit(DataState.Loading)
            try {
                val response = networkApi.uploadChatFile(
                    requestBody = requestBody
                )
                emit(DataState.Success(response))
            } catch (exception: Exception) {
                emit(DataState.Failed(ErrorResponse(exception)))
            }
        }
}