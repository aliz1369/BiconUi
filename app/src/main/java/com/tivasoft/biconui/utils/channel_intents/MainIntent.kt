package com.tivasoft.biconui.utils.channel_intents
/**
 * Generic class for telling viewModel what should be done and with what parameters.
 */
sealed class MainIntent {
    object UploadFiles : MainIntent()
}