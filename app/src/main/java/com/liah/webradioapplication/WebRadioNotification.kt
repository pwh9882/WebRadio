package com.liah.webradioapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startActivity

object WebRadioNotification {
    const val CHANNEL_ID = "foreground_service_channel" // 임의의 채널 ID

    fun createNotification(
            context: Context
    ): Notification {
        val notificationIntent = Intent(context, MainActivity::class.java)
//        notificationIntent.action = Actions.MAIN
        notificationIntent.action = Intent.ACTION_MAIN;
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, FLAG_UPDATE_CURRENT)

        // 각 버튼들에 관한 Intent
        val stopIntent = Intent(context, WebViewForegroundService::class.java)
        stopIntent.action = Actions.STOP
        val stopPendingIntent = PendingIntent.getService(context, 0, stopIntent, 0)

        val playIntent = Intent(context, WebViewForegroundService::class.java)
        playIntent.action = Actions.PLAY
        val playPendingIntent = PendingIntent.getService(context, 0, playIntent, 0)

        // 알림
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("WebRadio")
                .setContentText("~콘텐츠 내용~")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .addAction(NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", playPendingIntent))
                .addAction(NotificationCompat.Action(android.R.drawable.ic_media_pause, "Stop", stopPendingIntent))
                .setContentIntent(pendingIntent)
                .build()

        // Oreo 이상
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    "WebRadio",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
        return notification
    }

}