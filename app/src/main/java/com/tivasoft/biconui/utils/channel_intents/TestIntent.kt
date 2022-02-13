package com.tivasoft.biconui.utils.channel_intents

import com.tivasoft.biconui.data.model.network.requests.test.TestResultRequest

/**
 * Generic class for telling viewModel what should be done and with what parameters.
 */
sealed class TestIntent {
    class GetBackPack(val patientId: String?) : TestIntent()
    class GetSingleTest(val testId: String) : TestIntent()
    class SendTestResult(val testResult: TestResultRequest) : TestIntent()
}