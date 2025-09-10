package com.b0nn1e.youtube.player.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.annotation.VisibleForTesting
import com.b0nn1e.youtube.R
import com.b0nn1e.youtube.player.*
import com.b0nn1e.youtube.player.listeners.FullscreenListener
import com.b0nn1e.youtube.player.listeners.YouTubePlayerListener
import com.b0nn1e.youtube.player.options.IFramePlayerOptions
import com.b0nn1e.youtube.player.toFloat
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import androidx.core.graphics.createBitmap

/**
 * [YouTubePlayer] 的实现类，通过 WebView 调用 IFrame Player API 控制播放器。
 */
private class YouTubePlayerImpl(
    private val webView: WebView,
    private val callbacks: YouTubePlayerCallbacks
) : YouTubePlayer {
    /**
     * 主线程 Handler，确保 JavaScript 调用在主线程执行。
     */
    private val mainThread: Handler = Handler(Looper.getMainLooper())

    /**
     * 存储播放器监听器。
     */
    val listeners = mutableSetOf<YouTubePlayerListener>()

    override fun loadVideo(videoId: String, startSeconds: Float) = webView.invoke("loadVideo", videoId, startSeconds)
    override fun cueVideo(videoId: String, startSeconds: Float) = webView.invoke("cueVideo", videoId, startSeconds)
    override fun play() = webView.invoke("playVideo")
    override fun pause() = webView.invoke("pauseVideo")
    override fun nextVideo() = webView.invoke("nextVideo")
    override fun previousVideo() = webView.invoke("previousVideo")
    override fun playVideoAt(index: Int) = webView.invoke("playVideoAt", index)
    override fun setLoop(loop: Boolean) = webView.invoke("setLoop", loop)
    override fun setShuffle(shuffle: Boolean) = webView.invoke("setShuffle", shuffle)
    override fun mute() = webView.invoke("mute")
    override fun unMute() = webView.invoke("unMute")

    override fun isMutedAsync(callback: BooleanProvider) {
        val requestId = callbacks.registerBooleanCallback(callback)
        webView.invoke("getMuteValue", requestId)
    }

    override fun setVolume(volumePercent: Int) {
        require(volumePercent in 0..100) { "音量必须在 0 到 100 之间" }
        webView.invoke("setVolume", volumePercent)
    }

    override fun seekTo(time: Float) = webView.invoke("seekTo", time)
    override fun setPlaybackRate(playbackRate: PlayerConstants.PlaybackRate) = webView.invoke("setPlaybackRate", playbackRate.toFloat())
    override fun toggleFullscreen() = webView.invoke("toggleFullscreen")
    override fun addListener(listener: YouTubePlayerListener) = listeners.add(listener)
    override fun removeListener(listener: YouTubePlayerListener) = listeners.remove(listener)

    /**
     * 释放资源，清理监听器和主线程回调。
     */
    fun release() {
        listeners.clear()
        mainThread.removeCallbacksAndMessages(null)
    }

    /**
     * 调用 WebView 的 JavaScript 方法。
     * @param function JavaScript 函数名
     * @param args 参数列表
     */
    private fun WebView.invoke(function: String, vararg args: Any) {
        val stringArgs = args.map {
            if (it is String) {
                "'$it'"
            } else {
                it.toString()
            }
        }
        mainThread.post { loadUrl("javascript:$function(${stringArgs.joinToString(",")})") }
    }
}

/**
 * 全屏监听器的占位实现，空方法用于默认场景。
 */
internal object FakeWebViewYouTubeListener : FullscreenListener {
    override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {}
    override fun onExitFullscreen() {}
}

/**
 * [YouTubePlayer] 的 WebView 实现。播放器在 WebView 中运行，使用 IFrame Player API。
 */
internal class WebViewYouTubePlayer constructor(
    context: Context,
    private val listener: FullscreenListener,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr), YouTubePlayerBridge.YouTubePlayerBridgeCallbacks {

    /**
     * 构造函数，用于布局预览工具。
     * @param context 上下文
     */
    constructor(context: Context) : this(context, FakeWebViewYouTubeListener)

    /**
     * 存储播放器回调。
     */
    private val youTubePlayerCallbacks = YouTubePlayerCallbacks()

    /**
     * 播放器实现实例。
     */
    private val _youTubePlayer = YouTubePlayerImpl(this, youTubePlayerCallbacks)

    /**
     * 公开的播放器实例。
     */
    internal val youtubePlayer: YouTubePlayer get() = _youTubePlayer

    /**
     * 播放器初始化监听器。
     */
    private lateinit var youTubePlayerInitListener: (YouTubePlayer) -> Unit

    /**
     * 是否启用后台播放。
     */
    internal var isBackgroundPlaybackEnabled = false

    /**
     * JavaScript 桥接对象，用于 WebView 和播放器通信。
     */
    private val youTubePlayerBridge = YouTubePlayerBridge(this)

    /**
     * 初始化播放器，加载 IFrame Player API 和配置。
     * @param initListener 播放器就绪时的回调
     * @param playerOptions 播放器配置选项
     * @param videoId 可选，初始化后加载的视频 ID
     */
    internal fun initialize(initListener: (YouTubePlayer) -> Unit, playerOptions: IFramePlayerOptions?, videoId: String?) {
        youTubePlayerInitListener = initListener
        initWebView(playerOptions ?: IFramePlayerOptions.default, videoId)
    }

    override val listeners: Collection<YouTubePlayerListener> get() = _youTubePlayer.listeners.toSet()
    override fun getInstance(): YouTubePlayer = _youTubePlayer
    override fun onYouTubeIFrameAPIReady() = youTubePlayerInitListener(_youTubePlayer)
    fun addListener(listener: YouTubePlayerListener) = _youTubePlayer.listeners.add(listener)
    fun removeListener(listener: YouTubePlayerListener) = _youTubePlayer.listeners.remove(listener)

    /**
     * 销毁播放器，清理资源。
     */
    override fun destroy() {
        _youTubePlayer.release()
        super.destroy()
    }

    /**
     * 初始化 WebView，配置 JavaScript 和 HTML 内容。
     * @param playerOptions 播放器配置选项
     * @param videoId 可选，初始加载的视频 ID
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(playerOptions: IFramePlayerOptions, videoId: String?) {
        settings.apply {
            javaScriptEnabled = true
            mediaPlaybackRequiresUserGesture = false
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        addJavascriptInterface(youTubePlayerBridge, "YouTubePlayerBridge")
        addJavascriptInterface(youTubePlayerCallbacks, "YouTubePlayerCallbacks")

        val htmlPage = readHTMLFromUTF8File(resources.openRawResource(R.raw.ayp_youtube_player))
            .replace("<<injectedVideoId>>", if (videoId != null) { "'$videoId'" } else { "undefined" })
            .replace("<<injectedPlayerVars>>", playerOptions.toString())

        loadDataWithBaseURL(playerOptions.getOrigin(), htmlPage, "text/html", "utf-8", null)

        webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                super.onShowCustomView(view, callback)
                listener.onEnterFullscreen(view) { callback.onCustomViewHidden() }
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                listener.onExitFullscreen()
            }

            override fun getDefaultVideoPoster(): Bitmap? {
                val result = super.getDefaultVideoPoster()
                return result ?: createBitmap(1, 1, Bitmap.Config.RGB_565)
            }
        }
    }

    /**
     * 处理窗口可见性变化，允许后台播放时忽略 GONE 或 INVISIBLE。
     */
    override fun onWindowVisibilityChanged(visibility: Int) {
        if (isBackgroundPlaybackEnabled && (visibility == View.GONE || visibility == View.INVISIBLE)) {
            return
        }
        super.onWindowVisibilityChanged(visibility)
    }
}

/**
 * 从 UTF-8 编码的输入流读取 HTML 文件内容。
 * @param inputStream 输入流
 * @return HTML 内容字符串
 * @throws RuntimeException 如果解析 HTML 文件失败
 */
@VisibleForTesting
internal fun readHTMLFromUTF8File(inputStream: InputStream): String {
    inputStream.use {
        try {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "utf-8"))
            return bufferedReader.readLines().joinToString("\n")
        } catch (e: Exception) {
            throw RuntimeException("无法解析 HTML 文件。")
        }
    }
}