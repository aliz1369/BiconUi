package com.tivasoft.biconui.utils

import com.tivasoft.biconui.data.model.local.ui.chat.DownloadEntity

sealed class DownloadResult {
    object Idle : DownloadResult()
    data class Success(val entity: DownloadEntity) : DownloadResult()
    data class Error(val message: String, val cause: Exception? = null) : DownloadResult()
    data class Progress(val progress: Int) : DownloadResult()
}