package com.liah.webradioapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.liah.webradioapplication.RadioList.radioList

object WebRadioNotification {
    const val CHANNEL_ID = "foreground_service_channel" // 임의의 채널 ID

    fun createNotification(
            context: Context,
            position: Int = 0,
            radioSubTitle: String = ""
    ): Notification {

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.action = Intent.ACTION_MAIN;
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        notificationIntent.putExtra("read", "true")

        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, FLAG_UPDATE_CURRENT)



        // 각 버튼들에 관한 Intent
        val stopIntent = Intent(context, WebRadioService::class.java)
        stopIntent.action = Actions.STOP
        val stopPendingIntent = PendingIntent.getService(context, 0, stopIntent, 0)

        val playIntent = Intent(context, WebRadioService::class.java)
        playIntent.action = Actions.PLAY
        val playPendingIntent = PendingIntent.getService(context, 0, playIntent, 0)

        val exitIntent = Intent(context, WebRadioService::class.java)
        exitIntent.action = Actions.STOP_FOREGROUND
        val exitPendingIntent = PendingIntent.getService(context, 0, exitIntent, 0)

//        BitmapFactory.decodeResource(context.resources, R.drawable.)

        // 알림
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(radioList[position].radioTitle)
                .setContentText(radioSubTitle)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .addAction(NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", playPendingIntent))
                .addAction(NotificationCompat.Action(android.R.drawable.ic_media_pause, "Stop", stopPendingIntent))
                .addAction(NotificationCompat.Action(android.R.drawable.ic_menu_close_clear_cancel, "Exit", exitPendingIntent))
                .setContentIntent(pendingIntent)
                .setColor(Color.parseColor("#C0C0C0"))
                .setColorized(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.temp__small_))
                .setStyle(androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
                .build()
//        var notification = NotificationCompat.Builder(context, CHANNEL_ID).apply {
//
//
//            // Show controls on lock screen even when user hides sensitive content.
//            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            setContentIntent(pendingIntent)
//            setSmallIcon(R.mipmap.ic_launcher_round)
//
//            if ()
//
//
////                // Add media control buttons that invoke intents in your media service
////                .addAction(R.drawable.ic_pause, "Pause", stopPendingIntent) // #1
////                .addAction(R.drawable.ic_next, "Next", nextPendingIntent) // #2
////                // Apply the media style template
////                .setStyle(MediaNotificationCompat.MediaStyle()
////                        .setShowActionsInCompactView(1 /* #1: pause button \*/)
////                        .setMediaSession(mediaSession.getSessionToken()))
////                .setContentTitle("Wonderful music")
////                .setContentText("My Awesome Band")
////                .setLargeIcon(albumArtBitmap)
////                .build()
//        }

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