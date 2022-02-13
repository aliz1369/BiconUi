package com.tivasoft.biconui.utils.channel_intents

import com.tivasoft.biconui.data.model.network.requests.MeetRequest

sealed class MeetIntent {
    class Call(val entity: MeetRequest) : MeetIntent()
    class EndCall(val entity: MeetRequest) : MeetIntent()
}