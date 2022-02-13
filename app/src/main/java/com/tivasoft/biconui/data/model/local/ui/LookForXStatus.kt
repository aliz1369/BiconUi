package com.tivasoft.biconui.data.model.local.ui

enum class LookForXStatus(val status: String) {
    NEW_REQUEST("New Request"),
    ACCEPTED_FROM_DOCTOR("Accepted from Doctor"),
    REJECTED_FROM_DOCTOR("Rejected from Doctor"),
    ACCEPTED_FROM_ASSISTANT("Accepted from Assistant"),
    REJECTED_FROM_ASSISTANT("Rejected from Assistant"),
    DISCONNECTED("Disconnected"),
    ALREADY_CONNECTED("Connected to another Doctor")
}