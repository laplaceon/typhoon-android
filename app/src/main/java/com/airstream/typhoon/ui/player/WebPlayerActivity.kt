package com.airstream.typhoon.ui.player

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.airstream.typhoon.R

class WebPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webplayer)

        val webview: WebView = findViewById(R.id.webview)
        webview.webViewClient = WebViewClient()
        webview.settings.javaScriptEnabled = true
        webview.settings.javaScriptCanOpenWindowsAutomatically = false

        val link = intent.extras!!.getString("link")!!

        Log.d(TAG, "onCreate: $link")

        webview.loadUrl(link)
    }

    companion object {
        private const val TAG = "WebPlayerActivity"
    }
}