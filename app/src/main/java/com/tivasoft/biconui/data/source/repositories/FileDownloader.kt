package com.tivasoft.biconui.data.source.repositories

import com.tivasoft.biconui.data.model.local.ui.chat.DownloadEntity
import com.tivasoft.biconui.utils.DownloadResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

suspend fun HttpClient.downloadFile(entity: DownloadEntity): Flow<DownloadResult> = flow {
    try {
        val response = request<HttpResponse> {
            url(entity.url)
            method = HttpMethod.Get
        }


        val data = response.content.toByteArray()
        var offset = 0

        do {
            val currentRead = response.content.readAvailable(data, offset, data.size)
            offset += currentRead
            val progress = (offset * 100f / data.size).roundToInt()
            emit(DownloadResult.Progress(progress))
        } while (currentRead > 0)

        if (response.status.isSuccess()) {
            withContext(Dispatchers.IO) {
                entity.file.write(data)
            }
            emit(DownloadResult.Success(entity))
        } else {
            emit(DownloadResult.Error("File not downloaded"))
        }
    } catch (e: TimeoutCancellationException) {
        emit(DownloadResult.Error("Connection timed out", e))
    } catch (t: Exception) {
        emit(DownloadResult.Error("${t.message}:${t.cause}:${t}"))
    }
}