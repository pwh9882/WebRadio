package com.liah.webradioapplication.api

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.webkit.WebView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

class BackgroundWebView : WebView {
    //https://blog.yena.io/studynote/2020/05/13/Android-WebView.html
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context!!,
            attrs,
            defStyleAttr
    )

    init { // 다크모드 설정하는 init
//        setBackgroundColor(Color.parseColor("#222222"))
//        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
//            WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON);
//        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        if (visibility != View.GONE) super.onWindowVisibilityChanged(View.VISIBLE)
    }
}