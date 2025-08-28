package com.lpg.lpgapp

import android.Manifest
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val LOGIN_URL = "https://holy-rich-cat.ngrok-free.app/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestCameraPermission()

        webView = findViewById(R.id.webView)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.mediaPlaybackRequiresUserGesture = false

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Cek apakah URL yang dimuat adalah halaman login
                if (url == LOGIN_URL) {
                    view?.clearHistory()
                }
            }
        }

        webView.webChromeClient = MyWebChromeClient()
        webView.loadUrl(LOGIN_URL) // Muat URL awal adalah halaman login
    }

    override fun onBackPressed() {
        // Jika URL saat ini adalah halaman login
        if (webView.url == LOGIN_URL) {
            AlertDialog.Builder(this)
                .setMessage("Apakah yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Ya") { _, _ ->
                    finish()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else if (webView.canGoBack()) {
            webView.goBack() // Kembali ke halaman sebelumnya
        } else {
            // Jika tidak bisa kembali dan bukan halaman login, tampilkan pop-up konfirmasi
            AlertDialog.Builder(this)
                .setMessage("Apakah yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Ya") { _, _ ->
                    finish()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Izin diberikan
                } else {
                    // Izin ditolak
                }
                return
            }
        }
    }

    private class MyWebChromeClient : WebChromeClient() {
        override fun onPermissionRequest(request: PermissionRequest) {
            request.grant(request.resources)
        }
    }
}