package com.ejemplo.luckyred

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myWebView: WebView = findViewById(R.id.webview)

        // Configuraciones del navegador interno
        val webSettings: WebSettings = myWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.allowFileAccess = true
        webSettings.mediaPlaybackRequiresUserGesture = false

        myWebView.webViewClient = WebViewClient()

        // ¡EL PUENTE MÁGICO! Conecta tu HTML con el hardware del celular
        myWebView.addJavascriptInterface(WebAppInterface(this), "Android")

        // Cargar tu archivo HTML
        myWebView.loadUrl("file:///android_asset/index.html")
    }
}

// Clase que se encarga de hablar y vibrar usando el sistema del celular
class WebAppInterface(private val mContext: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(mContext, this)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("es", "ES") // Configura la voz en español
        }
    }

    @JavascriptInterface
    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    @JavascriptInterface
    fun vibrate(duration: Long) {
        val vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    @JavascriptInterface
    fun vibratePattern() {
        val pattern = longArrayOf(0, 500, 200, 500, 200, 1000)
        val vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }
}