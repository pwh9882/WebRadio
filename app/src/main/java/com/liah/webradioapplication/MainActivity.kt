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

    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(mBroadcastReceiver)
        unregisterReceiver(playerBroadcastReceiver)
    }
}