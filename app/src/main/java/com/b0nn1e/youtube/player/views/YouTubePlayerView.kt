package com.b0nn1e.youtube.player.views

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.b0nn1e.youtube.R
import com.b0nn1e.youtube.player.YouTubePlayer
import com.b0nn1e.youtube.player.listeners.*
import com.b0nn1e.youtube.player.options.IFramePlayerOptions
import com.b0nn1e.youtube.player.utils.loadOrCueVideo

private const val AUTO_INIT_ERROR = "YouTubePlayerView: 如果你想手动初始化此视图，需将 'enableAutomaticInitialization' 设置为 false。"

private val matchParent
    get() = FrameLayout.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT
    )

/**
 * 用户面向的 YouTube 播放器视图类，负责管理初始化、生命周期和自定义 UI。
 * 内部委托 [LegacyYouTubePlayerView] 处理核心逻辑。
 */
class YouTubePlayerView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SixteenByNineFrameLayout(context, attrs, defStyleAttr), LifecycleEventObserver {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)

    /**
     * 存储全屏监听器列表。
     */
    private val fullscreenListeners = mutableListOf<FullscreenListener>()

    /**
     * 内部全屏监听器，转发事件给所有注册监听器。
     */
    private val webViewFullscreenListener = object : FullscreenListener {
        override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
            if (fullscreenListeners.isEmpty()) {
                throw IllegalStateException("进入全屏需先注册 FullscreenListener。")
            }
            fullscreenListeners.forEach { it.onEnterFullscreen(fullscreenView, exitFullscreen) }
        }

        override fun onExitFullscreen() {
            if (fullscreenListeners.isEmpty()) {
                throw IllegalStateException("进入全屏需先注册 FullscreenListener。")
            }
            fullscreenListeners.forEach { it.onExitFullscreen() }
        }
    }

    /**
     * 内部遗留播放器视图，委托核心功能。
     */
    private val legacyTubePlayerView = LegacyYouTubePlayerView(context, webViewFullscreenListener)

    /**
     * 是否启用自动初始化。
     */
    var enableAutomaticInitialization: Boolean

    init {
        addView(legacyTubePlayerView, matchParent)

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.YouTubePlayerView, 0, 0)

        enableAutomaticInitialization = typedArray.getBoolean(R.styleable.YouTubePlayerView_enableAutomaticInitialization, true)
        val autoPlay = typedArray.getBoolean(R.styleable.YouTubePlayerView_autoPlay, false)
        val handleNetworkEvents = typedArray.getBoolean(R.styleable.YouTubePlayerView_handleNetworkEvents, true)
        val videoId = typedArray.getString(R.styleable.YouTubePlayerView_videoId)

        typedArray.recycle()

        if (autoPlay && videoId == null) {
            throw IllegalStateException("YouTubePlayerView: videoId 未设置但 autoPlay 为 true。此组合不允许。")
        }

        val youTubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                videoId?.let {
                    youTubePlayer.loadOrCueVideo(legacyTubePlayerView.canPlay && autoPlay, videoId, 0f)
                }
                youTubePlayer.removeListener(this)
            }
        }

        if (enableAutomaticInitialization) {
            legacyTubePlayerView.initialize(youTubePlayerListener, handleNetworkEvents)
        }
    }

    /**
     * 初始化播放器。需在开始使用前调用，如果启用自动初始化则无需手动调用。
     * @param youTubePlayerListener 播放器事件监听器
     * @param handleNetworkEvents 是否自动处理网络事件
     * @param playerOptions 播放器配置选项
     * @throws IllegalStateException 如果启用自动初始化
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener, handleNetworkEvents: Boolean, playerOptions: IFramePlayerOptions) {
        if (enableAutomaticInitialization) {
            throw IllegalStateException(AUTO_INIT_ERROR)
        } else {
            legacyTubePlayerView.initialize(youTubePlayerListener, handleNetworkEvents, playerOptions)
        }
    }

    /**
     * 初始化播放器。需在开始使用前调用，如果启用自动初始化则无需手动调用。
     * @param youTubePlayerListener 播放器事件监听器
     * @param handleNetworkEvents 是否自动处理网络事件
     * @throws IllegalStateException 如果启用自动初始化
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener, handleNetworkEvents: Boolean) {
        if (enableAutomaticInitialization) {
            throw IllegalStateException(AUTO_INIT_ERROR)
        } else {
            legacyTubePlayerView.initialize(youTubePlayerListener, handleNetworkEvents)
        }
    }

    /**
     * 初始化播放器，自动处理网络事件。
     * @param youTubePlayerListener 播放器事件监听器
     * @throws IllegalStateException 如果启用自动初始化
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener) {
        if (enableAutomaticInitialization) {
            throw IllegalStateException(AUTO_INIT_ERROR)
        } else {
            legacyTubePlayerView.initialize(youTubePlayerListener, true)
        }
    }

    /**
     * 获取播放器实例的回调。
     * 如果播放器已就绪，立即调用；否则等待就绪。
     * 每个回调仅调用一次。
     * @param youTubePlayerCallback 播放器就绪时的回调
     */
    fun getYouTubePlayerWhenReady(youTubePlayerCallback: YouTubePlayerCallback) = legacyTubePlayerView.getYouTubePlayerWhenReady(youTubePlayerCallback)

    /**
     * 使用自定义 UI 替换播放器的默认 UI。
     * 调用后需自行管理自定义 UI，警告：自定义 UI 可能违反 PlayStore 政策。
     * @param layoutId 自定义 UI 的布局 ID
     * @return 填充的自定义视图
     */
    fun inflateCustomPlayerUi(@LayoutRes layoutId: Int) = legacyTubePlayerView.inflateCustomPlayerUi(layoutId)

    /**
     * 设置自定义 UI 视图，替换默认 UI。
     * @param view 自定义 UI 视图
     */
    fun setCustomPlayerUi(view: View) = legacyTubePlayerView.setCustomPlayerUi(view)

    /**
     * 启用或禁用后台播放。
     * 注意：启用后台播放可能违反 YouTube 服务条款，不建议用于 PlayStore 应用。
     * @param enable 是否启用后台播放
     */
    fun enableBackgroundPlayback(enable: Boolean) = legacyTubePlayerView.enableBackgroundPlayback(enable)

    /**
     * 响应生命周期状态变化。
     * @param source 生命周期所有者
     * @param event 生命周期事件
     */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_STOP -> onStop()
            Lifecycle.Event.ON_DESTROY -> release()
            Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_ANY -> { }
        }
    }

    /**
     * 在销毁宿主 Fragment/Activity 前调用，或注册此视图为生命周期观察者。
     * 清理资源。
     */
    fun release() = legacyTubePlayerView.release()

    /**
     * 响应恢复事件。
     */
    private fun onResume() = legacyTubePlayerView.onResume()

    /**
     * 响应停止事件。
     */
    private fun onStop() = legacyTubePlayerView.onStop()

    /**
     * 添加播放器监听器。
     * @param youTubePlayerListener 播放器监听器
     */
    fun addYouTubePlayerListener(youTubePlayerListener: YouTubePlayerListener) = legacyTubePlayerView.webViewYouTubePlayer.addListener(youTubePlayerListener)

    /**
     * 移除播放器监听器。
     * @param youTubePlayerListener 播放器监听器
     */
    fun removeYouTubePlayerListener(youTubePlayerListener: YouTubePlayerListener) = legacyTubePlayerView.webViewYouTubePlayer.removeListener(youTubePlayerListener)

    /**
     * 添加全屏监听器。
     * @param fullscreenListener 全屏监听器
     */
    fun addFullscreenListener(fullscreenListener: FullscreenListener) = fullscreenListeners.add(fullscreenListener)

    /**
     * 移除全屏监听器。
     * @param fullscreenListener 全屏监听器
     */
    fun removeFullscreenListener(fullscreenListener: FullscreenListener) = fullscreenListeners.remove(fullscreenListener)

    /**
     * 设置视图宽度和高度为 MATCH_PARENT。
     */
    fun matchParent() {
        setLayoutParams(
            targetWidth = ViewGroup.LayoutParams.MATCH_PARENT,
            targetHeight = ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    /**
     * 设置视图宽度为 MATCH_PARENT，高度为 WRAP_CONTENT。
     */
    fun wrapContent() {
        setLayoutParams(
            targetWidth = ViewGroup.LayoutParams.MATCH_PARENT,
            targetHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * 设置布局参数。
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     */
    @Suppress("SameParameterValue")
    private fun setLayoutParams(targetWidth: Int, targetHeight: Int) {
        layoutParams = layoutParams.apply {
            width = targetWidth
            height = targetHeight
        }
    }
}