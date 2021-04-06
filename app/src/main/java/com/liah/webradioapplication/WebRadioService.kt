package com.liah.webradioapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.liah.webradioapplication.RadioList.radioList
import com.liah.webradioapplication.api.Radio
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.LocalDate
import java.util.*

class WebRadioService: Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private var mmIntentFilter: IntentFilter? = IntentFilter(Intent.ACTION_HEADSET_PLUG)
    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var isEarphoneON: Boolean = false
    private var earphoneDetected: Int = 0


    private var mMediaPlayer: MediaPlayer? = null
    private val binder = LocalBinder()
    private var position: Int = -1
    private var radioSubtitle = "리스트에서 선택해주세요"

    inner class LocalBinder: Binder() {
        fun getService(): WebRadioService = this@WebRadioService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Log.e(TAG, "Action Resceived = ${intent?.action}")
        // intent가 시스템에 의해 재생성되었을때 nullrkqtdlamfh Java에서는 null check 필수
        when (intent?.action) {
            Actions.START_FOREGROUND -> {
//                Log.e(TAG, "Start Forefround 인텐트를 받음")
                startForegroundService()
            }
            Actions.STOP_FOREGROUND -> {
//                Log.e(TAG, "Stop Forefround 인텐트를 받음")
                mMediaPlayer?.pause()
                val stopIntent = Intent()
                stopIntent.action = "stop"
                sendBroadcast(stopIntent)
                stopForegroundService()
            }
            Actions.STOP -> {
//                Log.e(TAG, "정지")
                mMediaPlayer?.pause()
                val stopIntent = Intent()
                stopIntent.action = "stop"
                sendBroadcast(stopIntent)
            }
            Actions.PLAY -> {
                mMediaPlayer?.start()
                val playIntent = Intent()
                playIntent.action = "play"
                sendBroadcast(playIntent)
            }
            Actions.PLAY0 -> {startForegroundService(0);position=0}
            Actions.PLAY1 -> {startForegroundService(1);position=1}
            Actions.PLAY2 -> {startForegroundService(2);position=2}
            Actions.PLAY3 -> {startForegroundService(3);position=3}
            Actions.PLAY4 -> {startForegroundService(4);position=4}
            Actions.PLAY5 -> {startForegroundService(5);position=5}
            Actions.PLAY6 -> {startForegroundService(6);position=6}
            Actions.PLAY7 -> {startForegroundService(7);position=7}
            Actions.PLAY8 -> {startForegroundService(8);position=8}
            Actions.PLAY9 -> {startForegroundService(9);position=9}
            Actions.PLAY10 -> {startForegroundService(10);position=10}
            Actions.PLAY11 -> {startForegroundService(11);position=11}

            Actions.GET_INFO -> {
                val infoIntent = Intent()
                infoIntent.action = "info"
                infoIntent.putExtra("position", "$position")
                infoIntent.putExtra("isPlaying", "${mMediaPlayer?.isPlaying}")
                infoIntent.putExtra("title", radioSubtitle)
                sendBroadcast(infoIntent)
            }
        }
        return START_STICKY
    }

    private fun initMediaPlayer(position: Int = 0) {
        // ...initialize the MediaPlayer here...
//        Log.e(TAG, "시작")
        val radio = radioList[position]

        var hlsUrl = radio.radioHlsSlug

        radioSubtitle = parseTitle(radio)
//            Log.e("radioSubTitle", radioSubtitle)
        if (hlsUrl == null) {
            hlsUrl = parseJSON(radio)
        }
        mMediaPlayer?.release()
        mMediaPlayer = null
        mMediaPlayer = MediaPlayer().apply {
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(hlsUrl)
            setOnPreparedListener(this@WebRadioService)
            prepareAsync()
        }
        val titleIntent = Intent()
        titleIntent.action = "title"
        titleIntent.putExtra("title", radioSubtitle)
        sendBroadcast(titleIntent)





    }

    /** Called when MediaPlayer is ready */
    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.release()
        mMediaPlayer = null
//        wifiLock.release()
    }

    private fun startForegroundService(position: Int = 0) {
        earphoneDetected = 0
        if (mBroadcastReceiver != null) unregisterReceiver(mBroadcastReceiver)
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                isEarphoneON = intent!!.getIntExtra("state", 0) > 0
                if (earphoneDetected++ > 0) {
                    if (isEarphoneON) {

//                        Log.e("이어폰 service log", "Earphone is plugged $earphoneDetected");

                    } else {
//                        Log.e("이어폰 service log", "Earphone is unPlugged $earphoneDetected");
                        if (mMediaPlayer?.isPlaying == true) {
                            mMediaPlayer?.pause()
                            val stopIntent = Intent()
                            stopIntent.action = "stop"
                            sendBroadcast(stopIntent)
                        }

//                    stopForegroundService()
                    }
                }
            }

        }
        registerReceiver(mBroadcastReceiver, mmIntentFilter)
        CoroutineScope(Dispatchers.Default).launch {
            initMediaPlayer(position)
            val notification = WebRadioNotification.createNotification(this@WebRadioService, position, radioSubtitle)
            startForeground(NOTIFICATION_ID, notification)
        }

    }

    private fun stopForegroundService() {
        if (mBroadcastReceiver != null) unregisterReceiver(mBroadcastReceiver)
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    companion object {
        const val TAG = "[WebRadioService]"
        const val NOTIFICATION_ID = 20
    }

    private fun parseJSON(radio: Radio): String = runBlocking {
        return@runBlocking withContext(Dispatchers.IO) {
            var hlsUrl: String = "http://58.234.158.60:1935/efmlive/myStream/playlist.m3u8"
            val doc = Jsoup.connect(radio.radioApiSlug)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Linux; U; Android 4.0.3; de-ch; HTC Sensation Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
                    .get()
//            Log.e("DOC: ", doc.text())
            var jsonText = doc.text()
            var jObject:JSONObject
            when (radio.radioType) {
                "SBS" -> {
                    jObject = JSONObject(jsonText)
                    hlsUrl = jObject.getJSONObject("onair").getJSONObject("source").getJSONObject("mediasource").get("mediaurl").toString()
                }
                "KBS" -> {
                    jObject = JSONObject(jsonText)
                    hlsUrl = jObject.getJSONArray("channel_item").getJSONObject(0).get("service_url").toString()
                }
                "MBC" -> {
                    jsonText = jsonText.substringBefore(")").substringAfter("(")
                    jObject = JSONObject(jsonText)
                    hlsUrl = jObject.get("AACLiveURL").toString()
                }
            }
//            Log.e("HLSURL: ", hlsUrl)
            hlsUrl
        }
    }
    private fun parseTitle(radio: Radio): String = runBlocking {
        return@runBlocking withContext(Dispatchers.IO){
//            Log.e("parseTitle", "")
            var titleText = ""
            var doc: Document

            when (radio.radioType) {
                "SBS" -> {
                    doc = Jsoup.connect(radio.radioApiSlug)
                            .header("Content-Type", "application/json;charset=UTF-8")
                            .method(Connection.Method.GET)
                            .ignoreContentType(true)
                            .userAgent("Mozilla/5.0 (Linux; U; Android 4.0.3; de-ch; HTC Sensation Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
                            .get()
                    var jsonText = doc.text()
                    var jObject = JSONObject(jsonText)
                    titleText = jObject.getJSONObject("onair").getJSONObject("info").get("title").toString()

                }
                "KBS" -> {
                    doc = Jsoup.connect("http://static.api.kbs.co.kr/mediafactory/v1/schedule/onair_now?rtype=jsonp&channel_code=21,22,24,25&local_station_code=00&callback=getChannelInfoList")
                            .header("Content-Type", "application/json;charset=UTF-8")
                            .method(Connection.Method.GET)
                            .ignoreContentType(true)
                            .userAgent("Mozilla/5.0 (Linux; U; Android 4.0.3; de-ch; HTC Sensation Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
                            .get()
                    var jsonText = doc.text()
                    jsonText = jsonText.substringAfter("(").substringBefore(");")
                    var jObject = JSONArray(jsonText)
                    titleText = jObject.getJSONObject(radioList.indexOf(radio)).getJSONArray("schedules").getJSONObject(0).get("program_title").toString()

                }
                "MBC" -> {
                    var url: String = if (radioList.indexOf(radio) == 4) {
                        "http://control.imbc.com/Schedule/Radio/Time?sType=FM"
                    } else
                        "http://control.imbc.com/Schedule/Radio/Time?sType=FM4U"
                    doc = Jsoup.connect(url)
                            .header("Content-Type", "application/json;charset=UTF-8")
                            .method(Connection.Method.GET)
                            .ignoreContentType(true)
                            .userAgent("Mozilla/5.0 (Linux; U; Android 4.0.3; de-ch; HTC Sensation Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
                            .get()
                    var jsonText = doc.text()
                    var jObject = JSONArray(jsonText)
                    titleText = jObject.getJSONObject(0).get("Title").toString()
                }
                "CBS" -> {
                    var url: String = if (radioList.indexOf(radio) == 8) {
                        "http://www.cbs.co.kr/cbsplayer/rainbow/widget/timetable.asp?ch=2"
                    } else
                        "http://www.cbs.co.kr/cbsplayer/rainbow/widget/timetable.asp?ch=4"
                    doc = Jsoup.connect(url)
                            .header("Content-Type", "application/json;charset=UTF-8")
                            .method(Connection.Method.POST)
                            .ignoreContentType(true)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36 Edg/89.0.774.68")
                            .get()
                    var localDate = Calendar.getInstance().time.hours*3600 + Calendar.getInstance().time.minutes*60 + Calendar.getInstance().time.seconds
//                    Log.e("time: ", localDate.toString())
                    var aspText = doc.text()
                    var textList = aspText.split(" ")

//                    Log.e("lines", textList.size.toString())

                    var start = 0
                    var end = 7200
                    var endIndex = 1
                    for (i: Int in 2 until textList.size){
//                        Log.e("ith: ", "===============")
//                        Log.e("ith: ", textList[i])
//                        Log.e("start: ", start.toString())
//                        Log.e("end: ", end.toString())
                        if (localDate in (start) until end+1) {
                            titleText = textList[endIndex+2]
                            break
                        }
                        if (start == end) {
                            end = textList[i-1].toInt()
                        }
                        if (textList[i] == end.toString()){
                            start = "$end".toInt()
                            endIndex = i+1
                        }
                    }
                    for (i: Int in endIndex+3 until textList.size){
                        if (textList[i].startsWith("http")) break
                        titleText += " ${textList[i]}"
                    }


                }
                "TBS" -> {
                    doc = Jsoup.connect(radio.radioWebSlug)
                            .header("Content-Type", "application/json;charset=UTF-8")
                            .method(Connection.Method.GET)
                            .ignoreContentType(true)
                            .userAgent("Mozilla/5.0 (Linux; U; Android 4.0.3; de-ch; HTC Sensation Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
                            .get()
                    titleText = doc.select("span.tit").text()
                }
            }
//            Log.e("title: ", titleText)
            titleText
        }
    }
}
