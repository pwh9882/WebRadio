package com.liah.webradioapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class WebViewForegroundService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "Action REceived = ${intent?.action}")
        // intent가 시스템에 의해 재생성되었을때 nullrkqtdlamfh Java에서는 null check 필수
        when (intent?.action) {
            Actions.START_FOREGROUND -> {
                Log.e(TAG, "Start Forefround 인텐트를 받음")
                startForegroundService()
            }
            Actions.STOP_FOREGROUND -> {
                Log.e(TAG, "Stop Forefround 인텐트를 받음")
                stopForegroundService()
            }
            Actions.STOP -> Log.e(TAG, "정지")
            Actions.PLAY -> Log.e(TAG, "시작")

        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = WebRadioNotification.createNotification(this)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val TAG = "[WebRadioService]"
        const val NOTIFICATION_ID = 20
    }

}
