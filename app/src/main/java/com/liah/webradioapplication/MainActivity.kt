package com.liah.webradioapplication

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.liah.webradioapplication.adapters.RadioListAdapter
import com.liah.webradioapplication.api.FullscreenableChromeClient
import com.liah.webradioapplication.api.Radio
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_radio.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        setContentView(R.layout.activity_main)

        val radioList = arrayOf(
                Radio(
                        "KBS 제1라디오", "FM 97.3㎒", "KBS",
                        "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=21#refresh",
                        "http://1radio.gscdn.kbs.co.kr/1radio_192_1.m3u8?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cDovLzFyYWRpby5nc2Nkbi5rYnMuY28ua3IvMXJhZGlvXzE5Ml8xLm0zdTgiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE2MTc2Mjc5Mjd9fX1dfQ__&Signature=eMkIg9iNEDoO0ZrmjQVgevYWvUsWAm0iSLlWoVWsqECT~5I86EOyKodPOfMulpUZ6muf~Hfk-osYZlrVLz~GL3K1~I9B6cC5CidYX2yiRRYY2dk7wI7iHf73ktPH716EU0kPiNAx9OHtTiTYCZlor0G-q8Pm0srTnXa0f0m-g2vFXsgCKWK0Br1WK~Yunf4EX-8BwAv2hLLbSo93G4XMY~T-h2~ZmYrrATczkqxXCQWrbhIIQReEljxBmFWdN4ygAXgpkYPl7NhFWA1L5X5US5DTS00TKCJMJykKuD2fX-3V1kmlAIQWQiNKFKdSvr~TA9k9Hy6ef16FFRH2lyFTVg__&Key-Pair-Id=APKAICDSGT3Y7IXGJ3TA"
                ),
                Radio(
                        "KBS 제2라디오", "FM 106.1㎒", "KBS",
                        "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=22#refresh",
                        "http://2radio.gscdn.kbs.co.kr/2radio_192_1.m3u8?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cDovLzJyYWRpby5nc2Nkbi5rYnMuY28ua3IvMnJhZGlvXzE5Ml8xLm0zdTgiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE2MTc2MjczNjF9fX1dfQ__&Signature=VJ1T75gnd2BZZRd3ajoBJviGPPemrMTJlbmHWnxbO6eFenwJwoJBTQC0MMbOvjIDbIc5vYj9ZbQ8ic0Ex~vSfaTAcFb0tW3VDgfQjgFA2uAjORvpmztZcd-r9INwVmTDQlgPKKs8ZpmJWB-EiwU5IGajrd5aN6jFyuYzWPxy7S1z9O0Eo845KfeCV8AmF8MKDs3XZd23LYRC3OzXEGHIYe0NBwm2scgJv-ptLGkhXRfk2XYVq-jWGSEej0tt5dZsV1kJ3XCR0rlzpZYT~GaDCUcGzau48wQW1uq1l10lIRA~Ss97SlKFst-3LBvVKcySmryfaI9IaloLJ~fTA-OPeA__&Key-Pair-Id=APKAICDSGT3Y7IXGJ3TA"
                ),
                Radio(
                        "KBS 1FM", "FM 93.1㎒", "KBS",
                        "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=24#refresh",
                        "http://1fm.gscdn.kbs.co.kr/1fm_192_1.m3u8?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cDovLzFmbS5nc2Nkbi5rYnMuY28ua3IvMWZtXzE5Ml8xLm0zdTgiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE2MTc2MjgzMzB9fX1dfQ__&Signature=lNKsu7dYmbdCM5Eo8vbq34HAV2vahY3pcNKSCHIlTnmQ5NUqvQDz09C4NVUqoaOTNVooVtwwAAQrjesuVosLhSn9bL~uy1ScaOrT9rADfEQKAOWOwZk4ZEZ8NfdMT-6VqdWR7I6KSzIo3QkcI~xmzufRY2sNYzEcXXJkAs3IOe2VKONl1gJDAp9xae36s1In2nz8-LXPF6PEXXdj3OJH1pJJawIyhGKpSVrsaEqioMkQfgB0xvh-V6Oh7w4MtAaYjVtQuvMHehyc0GwzWP00a5t6KhnEozXY54UyE-7kFZBu2q4IBi0L~TtU1eujKAci65YmBTz0Ajx8H6iVFOwuEg__&Key-Pair-Id=APKAICDSGT3Y7IXGJ3TA"
                ),
                Radio(
                        "KBS 2FM", "FM 89.1㎒", "KBS",
                        "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=25#refresh",
                        "http://2fm.gscdn.kbs.co.kr/2fm_192_1.m3u8?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cDovLzJmbS5nc2Nkbi5rYnMuY28ua3IvMmZtXzE5Ml8xLm0zdTgiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE2MTc2MjgzNzB9fX1dfQ__&Signature=nVv2csa54VkjpH3M~TmltgX0JbesVFjTjbHl9GqyXOPVOo2284B1x9KeJo6J35z8H2Ibu8LlmoUeon615aGdyOg8vABTNaTjZoFn0DW4K2SM9HYdvunG55mT~Ui6dxwo9m15G28EnZyqK9IsKLzHS6AJGxEgRcQiX-LJP4XnRphYAXILpzpftLVyo35Pl7NZd8bY3Xo8usG7TYJ43hrZABvReZeX4jxR78jIeMZjkOGmhq8BpC-M6DktQSLjuCkfxn4Z2MLrjYbOdrP8crU3aDNhcM-hEwPZgBIr898E88-oJbznYacaq0fc9v3rYOL8xtVdR7Q6Hb3SHzRTaPS2uA__&Key-Pair-Id=APKAICDSGT3Y7IXGJ3TA"
                ),
                Radio(
                        "MBC 라디오",
                        "FM 95.9㎒",
                        "MBC",
                        "http://mini.imbc.com/webapp_v3/mini.html?channel=sfm",
                        "http://183.111.24.125/hsfm_web/_definst_/sfm.stream/chunklist_w1060090897.m3u8?csu=false&tid=8cacaf1350b30a26a43e3c23e5b7854e"
                ),
                Radio(
                        "MBC FM4U",
                        "FM 91.9㎒",
                        "MBC",
                        "http://mini.imbc.com/webapp_v3/mini.html?channel=mfm",
                        "http://183.111.25.4/hmfm_web/_definst_/mfm.stream/chunklist_w2077962393.m3u8?csu=false&tid=6930f2367741c59bc38ab84f411b59ed"
                ),
                Radio(
                        "SBS 러브FM", "FM 103.5㎒", "SBS",
                        "http://play.sbs.co.kr/onair/pc/index.html?id=S08",
                        "http://radiolive.sbs.co.kr/lovepc/lovefm.stream/chunklist.m3u8?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MTc0NjMyMDUsInBhdGgiOiIvbG92ZWZtLnN0cmVhbSIsImR1cmF0aW9uIjotMSwidW5vIjoiNzJiNjE0ZGItMTdjNC00ODQ3LWFjYjctOGVlZDMzN2M2ZTFjIn0.5ferr1RFm6VfgokW45bVe7Uz993BQUwPYcET7BhtKlw&solsessionid=e28939423b53ff07f4dada0440ad5e99"
                ),
                Radio(
                        "SBS 파워FM", "FM 107.7㎒", "SBS",
                        "http://play.sbs.co.kr/onair/pc/index.html?id=S07",
                        "http://radiolive.sbs.co.kr/powerpc/powerfm.stream/chunklist.m3u8?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MTc0NjMxMTIsInBhdGgiOiIvcG93ZXJmbS5zdHJlYW0iLCJkdXJhdGlvbiI6LTEsInVubyI6IjI3ZDUzY2UyLWMzNDEtNDY2MS05MzM4LWQ2N2JjNDkzNGEyYiJ9.Bj1qsXaa5nFq104VDXCZ0VLk1UZSInMf7Esu7QuVX3Q&solsessionid=3ec76e1d02f29f82253321753cde3563"
                ),
                Radio(
                        "CBS 음악FM",
                        "FM 93.9㎒",
                        "CBS",
                        "http://www.cbs.co.kr/radio/frame/AodJwPlayer.asp#refresh",
                        "http://aac.cbs.co.kr/cbs939/_definst_/cbs939.stream/chunklist_w1649226862.m3u8"
                ),
                Radio(
                        "CBS 표준FM",
                        "FM 98.1㎒",
                        "CBS",
                        "http://www.cbs.co.kr/radio/frame/AodJwPlayer.asp",
                        "http://aac.cbs.co.kr/cbs981/_definst_/cbs981.stream/chunklist_w513628592.m3u8"
                )
        )

        rv_radioList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_radioList.setHasFixedSize(true)
        rv_radioList.adapter = RadioListAdapter(radioList, this)

        class CustomJavaScriptInterface{
            @JavascriptInterface
            fun getTitle(title: String) { //위 자바스크립트가 호출되면 여기로 html이 반환됨
                Log.e("title:??:", "${tv_info_radioTitle.text}")

                this@MainActivity.runOnUiThread(
                        Runnable {
                            tv_info_radioSubject.text = title
                        }
                )
            }
        }

        wv_backgroundWebview.apply {
//                        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            // 페이지 로딩을 위한 자바스크립트, dom 설정
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            // 파일 접근
            settings.databaseEnabled = true
            settings.allowFileAccess = true

            // 줌 컨트롤 관련
            settings.builtInZoomControls = true
            settings.setSupportZoom(true)
//            settings.displayZoomControls = true

            // 캐시모드
//            settings.cacheMode = WebSettings.LOAD_NO_CACHE
//            val appCachePath = applicationContext.cacheDir.absolutePath
//            settings.setAppCachePath(appCachePath)
//            settings.setAppCacheEnabled(true)

            // 화면크기에 따른 콘텐츠 크기 조정 여부
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            // father를 위한 라디오 pc페이지 로딩
            settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36"

            // 자바스크립트로 새 창 띄우기 허용
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            addJavascriptInterface(CustomJavaScriptInterface(), "Android")

            iv_radioInfoImage.setColorFilter(Color.parseColor("#9c9c9c"), PorterDuff.Mode.MULTIPLY)
//            Color.parseColor("#5c5c5c")
            iv_radioPlayerImage.setColorFilter(Color.parseColor("#9c9c9c"), PorterDuff.Mode.MULTIPLY)
            Glide.with(this@MainActivity)
                .load(R.drawable.info_background)
                .into(iv_radioInfoImage)
            Glide.with(this@MainActivity)
                .load(R.drawable.player_background)
                .into(iv_radioPlayerImage)
            // 클라이언트
            webViewClient = object : WebViewClient() {

                @RequiresApi(Build.VERSION_CODES.KITKAT)
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    var javascriptPlay: String = "javaScript:"
                    var javascriptPause: String = "javaScript:"
                    var javascriptTitle: String = "javaScript:"
                    var javascriptVolume: String = "javaScript:"
                    val curRadio = getCurRadio(getUrl(), radioList)
                    if (curRadio != null) {
                        when (curRadio.radioType){
                            "SBS" -> {
                                javascriptPlay =
                                        "javaScript:document.getElementById('sbs-onair-video-element-self_html5_api').play();"
                                javascriptPause =
                                        "javaScript:document.getElementById('sbs-onair-video-element-self_html5_api').pause();"
                                javascriptTitle =
                                        "javaScript:window.Android.getTitle(document.getElementsByClassName('oct_ppi_title')[0].innerText);"
                                javascriptVolume +=
                                        "document.getElementById('sbs-onair-video-element-self_html5_api').volume=1;"
                            }
                            "KBS" -> {
                                javascriptPlay = "javaScript:jwplayer('kbs-social-player').play();"
                                javascriptPause =
                                        "javaScript:jwplayer('kbs-social-player').pause();"
                                javascriptTitle =
                                        "javaScript:window.Android.getTitle(document.getElementById('episode-tit').innerText);"
                                javascriptVolume += "jwplayer('kbs-social-player').setVolume(100);"
                            }
                            "MBC" -> {
                                javascriptPlay =
                                        "javaScript: document.getElementsByClassName('btn-stop')[0].click();"
                                javascriptPause =
                                        "javaScript: document.getElementsByClassName('btn-stop')[0].click();"
                                javascriptTitle =
                                        "javaScript:window.Android.getTitle(document.getElementsByClassName('ui-center')[0].innerText);"
                                javascriptVolume +=
                                        "document.getElementById('jarvisAudioPlayer').volume=1;"
                            }
                            "CBS" -> {
                                javascriptPlay = if (curRadio.radioTitle == "CBS 음악FM") {
                                    "javaScript: document.getElementById('btnMFM').click();"
                                } else {
                                    "javaScript: document.getElementById('btnFM').click();"
                                }
                                javascriptPause = "javaScript: document.getElementById('lbStop').click();"
                                javascriptTitle = "javaScript:window.Android.getTitle(document.getElementById('ifrInfo').contentWindow.document.getElementsByClassName('text')[0].innerText);"
                                javascriptVolume +=
                                        "document.getElementsByClassName('jw-video')[0].volume=1;"
                            }
                        }
                    }

//                    tv_player_btn.setOnClickListener {
//                        if(it.tag.toString() == "play"){ // 현재 재생중
//                            it.tag = "pause"
//                            it.setBackgroundResource(R.drawable.ic_baseline_play_circle_96)
////                            wv_backgrounWebview.loadUrl(javascriptPause)
//                            wv_backgroundWebview.evaluateJavascript(javascriptPause, null)
//
//                            val intent = Intent(this@MainActivity, WebViewForegroundService::class.java)
//                            intent.action = Actions.STOP_FOREGROUND
//                            startService(intent)
//
//                        } else { // 현재 중지
//                            it.tag = "play"
//                            it.setBackgroundResource(R.drawable.ic_baseline_pause_circle_96)
//                            wv_backgroundWebview.evaluateJavascript(javascriptPlay, null)
//
//                            val intent = Intent(this@MainActivity, WebViewForegroundService::class.java)
//                            intent.action = Actions.START_FOREGROUND
//                            startService(intent)
//                        }
//
//                    }

//                    Handler().postDelayed(Runnable {
//                        //딜레이 후 시작할 코드 작성
//                        Log.e("TitleLoad", "!!!")
//                        Handler().postDelayed(Runnable {
////                            wv_backgroundWebview.loadUrl(javascriptTitle)
//                            wv_backgroundWebview.evaluateJavascript(javascriptTitle, null)
////                            tv_player_btn.isClickable = true
//                        }, 1000)
//                    }, 1500) // 1.5초 정도 딜레이를 준 후 시작

                }
            }
            webChromeClient = FullscreenableChromeClient(this@MainActivity)
        }



        tv_debug.setOnClickListener {
            var wbHeight: Int = if (wv_backgroundWebview.height > 1) 1
            else 400
            wv_backgroundWebview.updateLayoutParams {
                height = wbHeight
            }
            Log.e("Click!!!!", "debug")
        }
    }

    fun getCurRadio(radioUrl: String?, radioList: Array<Radio>): Radio?{
        for (radio in radioList)
            if (radio.radioWebSlug == radioUrl) return radio
        return null
    }





    override fun onBackPressed() {
        super.onBackPressed()
    }
}