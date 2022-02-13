package com.tivasoft.biconui.data.model.local.ui.chat

data class SocketMessage(
    val sender: String,
    val receiver: String,
    val content: String,
    val type: String = SocketMessageType.TEXT.type
)

enum class SocketMessageType(val type: String) {
    TEXT("text"),
    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio"),
    FILE("file"),
    GAME("game"),
    TEST("test"),
    BREATH("breath"),
    NOT_SUPPORTED(""),
    DATE("date"),
    UPLOADING("uploading")
}