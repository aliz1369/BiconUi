package com.tivasoft.biconui.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast

class VoiceUtils private constructor() {
    private object Holder {
        val INSTANCE = MediaPlayer()
    }

    companion object {
        val player: MediaPlayer by lazy { Holder.INSTANCE }

        fun pauseAudio() {
            player.apply {
                try {
                    pause()
                } catch (e: Exception) {
                    Log.e("TAG", "pause() failed")
                }
            }
        }

        fun playAudio(context: Context, uri: Uri) {
            player.apply {
                try {
                    setDataSource(context, uri)
                    prepare()
                    start()
                } catch (e: Exception) {
                    Toast.makeText(context, "Something went wrong.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}