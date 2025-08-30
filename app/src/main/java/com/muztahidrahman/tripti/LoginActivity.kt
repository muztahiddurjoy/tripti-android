package com.muztahidrahman.tripti

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.muztahidrahman.tripti.db.sharedpref.SharedPreferencesStorage
import com.muztahidrahman.tripti.ui.theme.TriptiTheme
import kotlinx.coroutines.launch

class LoginActivity: ComponentActivity(){

    private lateinit var cookieManager: CookieManager
    private var webView: WebView? = null
    private var storage: SharedPreferencesStorage? = null

    companion object {
        private const val TAG = "WebViewCookies"
        private const val LOGIN_URL = "https://tripti.bracu.ac.bd" // Replace with your URL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        storage = SharedPreferencesStorage(this)
        setupCookieManager()

        setContent {
            TriptiTheme {
                WebViewScreen()
            }
        }
    }

    private fun setupCookieManager() {
        cookieManager = CookieManager.getInstance().apply {
            setAcceptCookie(true)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WebViewScreen() {
        var isLoading by remember { mutableStateOf(true) }
        var progress by remember { mutableIntStateOf(0) }
        var currentUrl by remember { mutableStateOf(LOGIN_URL) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),

            ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Progress Bar
                if (isLoading && progress < 100) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // Current URL display (optional, for debugging)
                if (currentUrl.isNotEmpty()) {
                    Text(
                        text = "URL: ${currentUrl.subSequence(0,20.coerceAtMost(currentUrl.length))}...",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // WebView
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            setupWebView(this) { url, loadingState, progressValue ->
                                currentUrl = url
                                isLoading = loadingState
                                progress = progressValue
                            }
                            webView = this
                            loadUrl(LOGIN_URL)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(
        webView: WebView,
        onStateChange: (String, Boolean, Int) -> Unit
    ) {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true

                // Enable cookies
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                // User agent
                userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36"
            }

            // Set third-party cookies
            cookieManager.setAcceptThirdPartyCookies(this, true)

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    url?.let { currentUrl ->
                        Log.d(TAG, "Page loaded: $currentUrl")
                        onStateChange(currentUrl, false, 100)

                        // Collect cookies after page loads
                        collectCookies(currentUrl)

                        // Check if login is successful
                        if (isLoginSuccessful(currentUrl)) {
                            Log.d(TAG, "Login successful detected!")
                            handleLoginSuccess()
                        }
                    }
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    url?.let {
                        onStateChange(it, true, 0)
                    }
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    super.onReceivedError(view, request, error)
                    Log.e(TAG, "WebView error: ${error?.description}")
                    onStateChange("Error loading page", false, 100)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return false // Allow WebView to handle all redirects
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    view?.url?.let { url ->
                        onStateChange(url, newProgress < 100, newProgress)
                    }
                }

                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    Log.d(TAG, "Console: ${consoleMessage?.message()}")
                    return true
                }
            }
        }
    }

    private fun collectCookies(url: String) {
        val cookies = cookieManager.getCookie(url)
        if(cookies.isEmpty()){
            Log.d(TAG, "No cookies found for $url")
            return
        }
            storage?.saveCookies(cookies)

    }
    private fun isLoginSuccessful(url: String): Boolean {
        // Customize this logic based on your application
        return when {
            url.contains("dashboard") -> true
            url.contains("/home") -> true
            url.contains("/profile") -> true
            url.contains("success") -> true
            else -> false
        }
    }

    private fun handleLoginSuccess() {
        Log.d(TAG, "Handling login success...")

        // Get all stored cookies
        val allCookies = storage?.getCookies()

        lifecycleScope.launch {
            Log.d(TAG, "All stored cookies: $allCookies")
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
            finish()
        }
    }







    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        webView?.let { wv ->
            if (wv.canGoBack()) {
                wv.goBack()
            } else {
                super.onBackPressed()
            }
        } ?: super.onBackPressed()
    }
}