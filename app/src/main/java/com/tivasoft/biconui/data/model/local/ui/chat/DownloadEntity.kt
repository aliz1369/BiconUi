package com.tivasoft.biconui.data.model.local.ui.chat

import java.io.OutputStream

data class DownloadEntity(
    val file: OutputStream,
    val url: String
)