package com.tivasoft.biconui.utils

import android.Manifest


object Constants {
    const val BASE_URL = "http://195.201.73.13:8000/api/v1/"
    const val SOCKET_URL = "https://makerwebsite.ir"
    const val MEET_URL = "https://meet.makerwebsite.ir/"

    const val SOCKET_CONNECTED = "connected"
    const val SOCKET_JOIN = "join"
    const val SOCKET_MESSAGE = "message"
    const val SOCKET_LEAVE = "leave"
    const val SOCKET_TYPING = "is-typing"

    const val DATABASE_NAME = "ChatDatabase"

    const val IMAGE_BASE_URL = ""
    const val CONNECT_TIME_OUT: Long = 30
    const val READ_TIME_OUT: Long = 30
    const val WRITE_TIME_OUT: Long = 30

    const val LIMIT_LIST_SIZE = 10

    const val SCHEDULE_REQUEST_CODE = 321
    const val SCHEDULE_RESULT_CODE = 123

    const val TAG = "cameraX"
    const val FILE_NAME_FORMAT = "yy-mm-dd-HH-mm-ss-SSS"
    const val REQUEST_CODE_PERMISSION = 123
    val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)

    @JvmField
    var ACCESS_TOKEN = ""
}