package com.b0nn1e.youtube.player.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.LayoutRes
import com.b0nn1e.youtube.player.PlayerConstants
import com.b0nn1e.youtube.player.YouTubePlayer
import com.b0nn1e.youtube.player.listeners.*
import com.b0nn1e.youtube.player.options.IFramePlayerOptions
import com.b0nn1e.youtube.player.utils.NetworkObserver
import com.b0nn1e.youtube.player.utils.PlaybackResumer

/**
 * YouTubePlayerView 的遗留内部实现。
 * 用户面向的 YouTubePlayerView 将大部分操作委托给此类。
 */
internal class LegacyYouTubePlayerView(
    context: Context,
    listener: FullscreenListener,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SixteenByNineFrameLayout(context, attrs, defStyleAttr) {

    /**
     * 次构造函数，使用默认的占位全屏监听器。
     * @param context 上下文
     */
    constructor(context: Context) : this(context, FakeWebViewYouTubeListener, null, 0)

    /**
     * 基于 WebView 的播放器核心实现。
     */
    internal val webViewYouTubePlayer = WebViewYouTubePlayer(context, listener)

    /**
     * 网络状态观察者，用于监听网络变化。
     */
    private val networkObserver = NetworkObserver(context.applicationContext)

    /**
     * 播放恢复工具，用于处理网络中断后的播放恢复。
     */
    private val playbackResumer = PlaybackResumer()

    /**
     * 标记播放器是否初始化完成。
     */
    internal var isYouTubePlayerReady = false

    /**
     * 存储初始化逻辑，延迟执行以等待网络或生命周期条件。
     */
    private var initialize = { }

    /**
     * 存储待播放器就绪时触发的回调。
     */
    private val youTubePlayerCallbacks = mutableSetOf<YouTubePlayerCallback>()

    /**
     * 是否允许播放，与生命周期状态相关。
     */
    internal var canPlay = true
        private set

    init {
        // 添加 WebView 播放器视图，填充整个布局
        addView(
            webViewYouTubePlayer,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )
        // 添加播放恢复监听器
        webViewYouTubePlayer.addListener(playbackResumer)

        // 防止后台播放：如果播放中但不符合播放条件，则暂停
        webViewYouTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                if (state == PlayerConstants.PlayerState.PLAYING && !isEligibleForPlayback()) {
                    youTubePlayer.pause()
                }
            }
        })

        // 处理播放器就绪：触发回调并清理
        webViewYouTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                isYouTubePlayerReady = true
                youTubePlayerCallbacks.forEach { it.onYouTubePlayer(youTubePlayer) }
                youTubePlayerCallbacks.clear()
                youTubePlayer.removeListener(this)
            }
        })

        // 监听网络变化：网络恢复时初始化或恢复播放
        networkObserver.listeners.add(object : NetworkObserver.Listener {
            override fun onNetworkAvailable() {
                if (!isYouTubePlayerReady) {
                    initialize()
                } else {
                    playbackResumer.resume(webViewYouTubePlayer.youtubePlayer)
                }
            }

            override fun onNetworkUnavailable() {
                // 可添加日志记录网络不可用事件
            }
        })
    }

    /**
     * 初始化播放器。必须在开始使用播放器前调用此方法。
     * @param youTubePlayerListener 播放器事件监听器
     * @param handleNetworkEvents 是否自动处理网络事件（true：注册网络监听器；false：需自行处理网络事件）
     * @param playerOptions 播放器配置选项，可为 null
     * @param videoId 可选，初始化后立即加载的视频 ID
     * @throws IllegalStateException 如果播放器已初始化
     */
    fun initialize(
        youTubePlayerListener: YouTubePlayerListener,
        handleNetworkEvents: Boolean,
        playerOptions: IFramePlayerOptions,
        videoId: String?
    ) {
        if (isYouTubePlayerReady) {
            throw IllegalStateException("此 YouTubePlayerView 已初始化。")
        }
        if (handleNetworkEvents) {
            networkObserver.observeNetwork()
        }
        initialize = {
            webViewYouTubePlayer.initialize({ it.addListener(youTubePlayerListener) }, playerOptions, videoId)
        }
        if (!handleNetworkEvents) {
            initialize()
        }
    }

    /**
     * 初始化播放器，使用默认视频 ID（null）。
     * @param youTubePlayerListener 播放器事件监听器
     * @param handleNetworkEvents 是否自动处理网络事件
     * @param playerOptions 播放器配置选项
     * @see initialize
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener, handleNetworkEvents: Boolean, playerOptions: IFramePlayerOptions) =
        initialize(youTubePlayerListener, handleNetworkEvents, playerOptions, null)

    /**
     * 初始化播放器，使用默认配置。
     * @param youTubePlayerListener 播放器事件监听器
     * @param handleNetworkEvents 是否自动处理网络事件
     * @see initialize
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener, handleNetworkEvents: Boolean) =
        initialize(youTubePlayerListener, handleNetworkEvents, IFramePlayerOptions.default)

    /**
     * 初始化播放器，自动处理网络事件。
     * @param youTubePlayerListener 播放器事件监听器
     * @see initialize
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener) = initialize(youTubePlayerListener, true)

    /**
     * 获取播放器实例的回调。
     * 如果播放器已就绪，立即调用回调；否则将回调加入等待列表。
     * 每个回调仅调用一次。
     * @param youTubePlayerCallback 播放器就绪时的回调
     */
    fun getYouTubePlayerWhenReady(youTubePlayerCallback: YouTubePlayerCallback) {
        if (isYouTubePlayerReady) {
            youTubePlayerCallback.onYouTubePlayer(webViewYouTubePlayer.youtubePlayer)
        } else {
            youTubePlayerCallbacks.add(youTubePlayerCallback)
        }
    }

    /**
     * 使用自定义 UI 替换播放器的默认 UI。
     * 调用后需自行管理自定义 UI，默认控制器将不可用。
     * @param layoutId 自定义 UI 的布局 ID
     * @return 填充的自定义视图
     */
    fun inflateCustomPlayerUi(@LayoutRes layoutId: Int): View {
        removeViews(1, childCount - 1)
        return inflate(context, layoutId, this)
    }

    /**
     * 设置自定义 UI 视图，替换默认 UI。
     * @param view 自定义 UI 视图
     */
    fun setCustomPlayerUi(view: View) {
        removeViews(1, childCount - 1)
        addView(view)
    }

    /**
     * 在销毁宿主 Fragment/Activity 前调用，或将此视图注册为生命周期观察者。
     * 清理网络观察者和播放器视图资源。
     */
    fun release() {
        networkObserver.destroy()
        removeView(webViewYouTubePlayer)
        webViewYouTubePlayer.removeAllViews()
        webViewYouTubePlayer.destroy()
    }

    /**
     * 响应生命周期恢复事件，允许播放并通知播放恢复工具。
     */
    internal fun onResume() {
        playbackResumer.onLifecycleResume()
        canPlay = true
    }

    /**
     * 响应生命周期停止事件，暂停播放并通知播放恢复工具。
     */
    internal fun onStop() {
        webViewYouTubePlayer.youtubePlayer.pause()
        playbackResumer.onLifecycleStop()
        canPlay = false
    }

    /**
     * 检查播放器是否允许播放，考虑 [WebViewYouTubePlayer.isBackgroundPlaybackEnabled] 属性。
     * @return true 表示允许播放，false 表示不允许
     */
    internal fun isEligibleForPlayback(): Boolean {
        return canPlay || webViewYouTubePlayer.isBackgroundPlaybackEnabled
    }

    /**
     * 启用或禁用后台播放。
     * 注意：启用后台播放可能违反 YouTube 服务条款，不建议用于 Play Store 应用。
     * @param enable 是否启用后台播放
     */
    fun enableBackgroundPlayback(enable: Boolean) {
        webViewYouTubePlayer.isBackgroundPlaybackEnabled = enable
    }
}