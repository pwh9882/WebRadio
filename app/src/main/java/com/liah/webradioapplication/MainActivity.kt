package com.liah.webradioapplication

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.liah.webradioapplication.adapters.RadioListAdapter
import com.liah.webradioapplication.api.FullscreenableChromeClient
import com.liah.webradioapplication.api.Radio
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_radio.*

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val radioList = arrayOf(
            Radio(
                "KBS 제1라디오", "FM 97.3㎒", "KBS",
                "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=21#refresh"
            ),
            Radio(
                "KBS 제2라디오", "FM 106.1㎒", "KBS",
                "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=22#refresh"
            ),
            Radio(
                "KBS 1FM", "FM 93.1㎒", "KBS",
                "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=24#refresh"
            ),
            Radio(
                "KBS 2FM", "FM 89.1㎒", "KBS",
                "http://onair.kbs.co.kr/index.html?sname=onair&stype=live&ch_code=25#refresh"
            ),
            Radio(
                "MBC 라디오",
                "FM 95.9㎒",
                "MBC",
                "http://mini.imbc.com/webapp_v3/mini.html?channel=sfm"
            ),
            Radio(
                "MBC FM4U",
                "FM 91.9㎒",
                "MBC",
                "http://mini.imbc.com/webapp_v3/mini.html?channel=mfm"
            ),
            Radio(
                "SBS 러브FM", "FM 103.5㎒", "SBS",
                "http://play.sbs.co.kr/onair/pc/index.html?id=S08"
            ),
            Radio(
                "SBS 파워FM", "FM 107.7㎒", "SBS",
                "http://play.sbs.co.kr/onair/pc/index.html?id=S07"
            ),
            Radio(
                "CBS 음악FM",
                "FM 93.9㎒",
                "CBS",
                "http://www.cbs.co.kr/radio/frame/AodJwPlayer.asp#refresh"
            ),
            Radio(
                "CBS 표준FM",
                "FM 98.1㎒",
                "CBS",
                "http://www.cbs.co.kr/radio/frame/AodJwPlayer.asp"
            )
        )

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

        wv_backgrounWebview.apply {
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

                    tv_player_btn.setOnClickListener {
                        if(it.tag.toString() == "play"){ // 현재 재생중
                            it.tag = "pause"
                            it.setBackgroundResource(R.drawable.ic_baseline_play_circle_96)
                            wv_backgrounWebview.loadUrl(javascriptPause)

                        } else { // 현재 중지
                            it.tag = "play"
                            it.setBackgroundResource(R.drawable.ic_baseline_pause_circle_96)
                            wv_backgrounWebview.loadUrl(javascriptPlay)
                        }

                    }

                    Handler().postDelayed(Runnable {
                        wv_backgrounWebview.loadUrl(javascriptVolume)
                        wv_backgrounWebview.loadUrl(javascriptTitle)
                        tv_loading.text = ""
                        //딜레이 후 시작할 코드 작성
                        tv_player_btn.performClick()
                        Log.e("AutoLoad", "!!!")
                        tv_player_btn.isClickable = true
                    }, 1000) // 0.1초 정도 딜레이를 준 후 시작

//                    btn_radioPlay.performClick()
                }
            }
            webChromeClient = FullscreenableChromeClient(this@MainActivity)
        }

        rv_radioList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_radioList.setHasFixedSize(true)
        rv_radioList.adapter = RadioListAdapter(radioList, this)



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