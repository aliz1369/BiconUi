package com.tivasoft.biconui

import me.pushy.sdk.Pushy
import android.content.Intent
import android.graphics.Color
import android.content.Context
import android.app.PendingIntent
import android.media.RingtoneManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import com.tivasoft.biconui.ui.MainActivity

class PushReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var notificationTitle = "Bicon"
        var notificationText = "You Have a New Call"
        var requestCode = -1
        var id = -1
        val contentIntent = Intent(context, MainActivity::class.java)

        if (intent.hasExtra("romName") && intent.hasExtra("type")) {
            requestCode = 1111
            id = 1
            contentIntent.putExtra("roomName", intent.getStringExtra("romName")!!)
            contentIntent.putExtra("type", intent.getStringExtra("type")!!)
        } else if (intent.hasExtra("fullName") && intent.hasExtra("content")) {
            requestCode = 2222
            id = 2
            notificationTitle = intent.getStringExtra("fullName")!!
            notificationText = intent.getStringExtra("content")!!
            contentIntent.putExtra("name", notificationTitle)
        }

        if (requestCode != -1) {
            //todo: channel id
            val builder = NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(longArrayOf(0, 400, 250, 400))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(
                    PendingIntent.getActivity(
                        context, requestCode, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )

            Pushy.setNotificationChannel(builder, context)

            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(id, builder.build())
        }
    }
}