package com.liah.webradioapplication

import android.content.*
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.liah.webradioapplication.RadioList.radioList
import com.liah.webradioapplication.adapters.RadioListAdapter
import com.liah.webradioapplication.api.FullscreenableChromeClient
import com.liah.webradioapplication.api.Radio
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_radio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    // UI 업데이트 감지
    private var playerBroadcastReceiver: BroadcastReceiver? = null

    // 이어폰 감지
    private var mIntentFilter: IntentFilter? = IntentFilter(Intent.ACTION_HEADSET_PLUG)
    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var isEarphoneON: Boolean = false

    // 백그라운드 서비스
    private var mService: WebRadioService? = null
    private var mBound = false
    /** Defines callbacks for service binding, passed to bindService()  */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as WebRadioService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        setContentView(R.layout.activity_main)

        rv_radioList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_radioList.setHasFixedSize(true)
        rv_radioList.adapter = RadioListAdapter(radioList, this)

        // 재생 정지 감지
        var playerIntentFilter: IntentFilter? = IntentFilter()
        playerIntentFilter!!.addAction("play")
        playerIntentFilter!!.addAction("stop")
        playerIntentFilter!!.addAction("info")
        playerIntentFilter!!.addAction("title")

        playerBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action){
                    "play" -> {
                        Log.e("Intent: ", "play")
                        tv_player_btn.tag = "play"
                        tv_player_btn.setBackgroundResource(R.drawable.ic_baseline_pause_circle_96)
                    }
                    "stop" -> {
                        Log.e("Intent: ", "stop")
                        tv_player_btn.tag = "pause"
                        tv_player_btn.setBackgroundResource(R.drawable.ic_baseline_play_circle_96)
                    }
                    "info" -> {
                        val position = intent.getStringExtra("position")?.toInt()
                        val isPlaying = intent.getStringExtra("isPlaying")?.toBoolean()
                        if (position != null) {
                            if (position >= 0){
                                val radio = radioList[position!!]

                                tv_info_radioTitle.text = radio.radioTitle
                                tv_info_radioFreq.text = radio.radioFreq
                                tv_info_radioFreq.setTextColor(Color.parseColor(Colors.colorList[position]))

                                tv_player_btn.isClickable = true
                                if (isPlaying == true){
                                    tv_player_btn.tag = "play"
                                    tv_player_btn.setBackgroundResource(R.drawable.ic_baseline_pause_circle_96)
                                } else {
                                    tv_player_btn.tag = "pause"
                                    tv_player_btn.setBackgroundResource(R.drawable.ic_baseline_play_circle_96)
                                }

                                tv_info_radioSubject.text = intent.getStringExtra("title")

                                tv_player_btn.setOnClickListener {
                                    if(it.tag.toString() == "play"){ // 현재 재생중
                                        it.tag = "pause"
                                        it.setBackgroundResource(R.drawable.ic_baseline_play_circle_96)

                                        val intent = Intent(this@MainActivity, WebRadioService::class.java)
                                        intent.action = Actions.STOP_FOREGROUND
                                        startService(intent)


                                    } else { // 현재 중지
                                        it.tag = "play"
                                        it.setBackgroundResource(R.drawable.ic_baseline_pause_circle_96)

                                        val intent = Intent(this@MainActivity, WebRadioService::class.java)
                                        intent.action = "com.liah.webradioapplication.action.play$position"
                                        startService(intent)
                                    }
                                }

                            }

                        }

                    }
                    "title" -> {
                        tv_info_radioSubject.text = intent.getStringExtra("title")
                    }
                }
            }
        }
        registerReceiver(playerBroadcastReceiver, playerIntentFilter)

        // Notification 켜진 상태에서 새 창 열기
        val intent = Intent(this, WebRadioService::class.java)
        intent.action = Actions.GET_INFO
        startService(intent)


        // 이어폰 감지
//        mBroadcastReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                isEarphoneON = intent!!.getIntExtra("state", 0) > 0
//
//                if (isEarphoneON) {
//
////                    Log.e("이어폰 log", "Earphone is plugged");
//
//                } else {
////                    Log.e("이어폰 log", "Earphone is unPlugged")
//                    if(tv_player_btn.tag.toString() == "play"){ // 현재 재생중
//                        tv_player_btn.tag = "pause"
//                        tv_player_btn.setBackgroundResource(R.drawable.ic_baseline_play_circle_96)
//                    }
//                }
//            }
//
//        }
//        registerReceiver(mBroadcastReceiver, mIntentFilter)

        iv_radioInfoImage.setColorFilter(Color.parseColor("#9c9c9c"), PorterDuff.Mode.MULTIPLY)
//            Color.parseColor("#5c5c5c")
        iv_radioPlayerImage.setColorFilter(Color.parseColor("#9c9c9c"), PorterDuff.Mode.MULTIPLY)
        Glide.with(this@MainActivity)
            .load(R.drawable.info_background)
            .into(iv_radioInfoImage)
        Glide.with(this@MainActivity)
            .load(R.drawable.player_background)
            .into(iv_radioPlayerImage)

//        tv_debug.setOnClickListener {
//            var wbHeight: Int = if (wv_backgroundWebview.height > 1) 1
//            else 400
//            wv_backgroundWebview.updateLayoutParams {
//                height = wbHeight
//            }
////            Log.e("Click!!!!", "debug")
//        }

        tv_exitApp.setOnClickListener {
            val intent = Intent(this, WebRadioService::class.java)
            intent.action = Actions.STOP_FOREGROUND
            startService(intent)
//            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
//            if (mBound) unbindService(mConnection); mBound = false
            moveTaskToBack(true)
            exitProcess(-1)
        }


        val resetCal = Calendar.getInstance()
        resetCal.set(Calendar.MINUTE, Calendar.getInstance().time.minutes+1)
        resetCal.set(Calendar.SECOND, 0)

        val date: Date = resetCal.time

//        val timerTask: TimerTask = object : TimerTask() {
//            override fun run() {
//                var curRadio: Radio? = null
//                for(radio: Radio in radioList){
//                    if (radio.radioTitle == tv_info_radioTitle.text) curRadio = radio
//                }
//                val updatedRadioSubtitle = parseTitle(curRadio)
//                Log.e("${tv_info_radioSubject.text}", updatedRadioSubtitle)
//                if (tv_info_radioSubject.text != updatedRadioSubtitle){
////                    val notification = WebRadioNotification.createNotification(this@WebRadioService, position, radioSubtitle)
////                    startForeground(WebRadioService.NOTIFICATION_ID, notification)
//                    Log.e("Title Changed!", "$updatedRadioSubtitle")
//                }
//                Log.e("TIMER", "${Date()}, $updatedRadioSubtitle")
//            }
//        }
//        val timer = Timer(true)
//        timer.scheduleAtFixedRate(timerTask, date, 60000) // 1분마다

    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(mBroadcastReceiver)
        unregisterReceiver(playerBroadcastReceiver)
    }

    fun parseTitle(radio: Radio?): String = runBlocking {
        return@runBlocking withContext(Dispatchers.IO){
//            Log.e("parseTitle", "")
            var titleText = "리스트에서 선택해주세요"
            val doc: Document
            if (radio != null) {
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
                            else if(textList[i].startsWith("/sermon/")) break
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
                    "EBS" -> {
                        doc = Jsoup.connect("https://www.ebs.co.kr/onair/cururentOnair.json?channelCd=RADIO")
                                .header("Content-Type", "application/json;charset=UTF-8")
                                .method(Connection.Method.GET)
                                .ignoreContentType(true)
                                .userAgent("Mozilla/5.0 (Linux; U; Android 4.0.3; de-ch; HTC Sensation Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
                                .get()
                        var jsonText = doc.text()
                        var jObject = JSONObject(jsonText).getJSONObject("nowProgram")
                        titleText = jObject.get("title").toString()
                    }
                }
            }
            titleText
        }
    }
}