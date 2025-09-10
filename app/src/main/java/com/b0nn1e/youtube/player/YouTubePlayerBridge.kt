package com.b0nn1e.youtube.player

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import androidx.annotation.RestrictTo
import com.b0nn1e.youtube.player.listeners.YouTubePlayerListener

/**
 * 用于 JavaScript 和 Java/Kotlin 通信的桥接类。
 * 通过 IFrame Player API 接收播放器事件并分发给监听器。
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class YouTubePlayerBridge(private val youTubePlayerOwner: YouTubePlayerBridgeCallbacks) {

    companion object {
        // JavaScript 播放器状态常量，与 IFrame Player API 一致
        private const val STATE_UNSTARTED = "UNSTARTED"
        private const val STATE_ENDED = "ENDED"
        private const val STATE_PLAYING = "PLAYING"
        private const val STATE_PAUSED = "PAUSED"
        private const val STATE_BUFFERING = "BUFFERING"
        private const val STATE_CUED = "CUED"

        // JavaScript 播放质量常量
        private const val QUALITY_SMALL = "small"
        private const val QUALITY_MEDIUM = "medium"
        private const val QUALITY_LARGE = "large"
        private const val QUALITY_HD720 = "hd720"
        private const val QUALITY_HD1080 = "hd1080"
        private const val QUALITY_HIGH_RES = "highres"
        private const val QUALITY_DEFAULT = "default"

        // JavaScript 播放速率常量
        private const val RATE_0_25 = "0.25"
        private const val RATE_0_5 = "0.5"
        private const val RATE_0_75 = "0.75"
        private const val RATE_1 = "1"
        private const val RATE_1_25 = "1.25"
        private const val RATE_1_5 = "1.5"
        private const val RATE_1_75 = "1.75"
        private const val RATE_2 = "2"

        // JavaScript 错误代码常量
        private const val ERROR_INVALID_PARAMETER_IN_REQUEST = "2"
        private const val ERROR_HTML_5_PLAYER = "5"
        private const val ERROR_VIDEO_NOT_FOUND = "100"
        private const val ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER1 = "101"
        private const val ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER2 = "150"
        private const val ERROR_REQUEST_MISSING_HTTP_REFERER = "153"
    }

    /**
     * 主线程 Handler，确保事件分发在主线程执行。
     */
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

    /**
     * 桥接回调接口，提供监听器集合、播放器实例和 API 就绪回调。
     */
    interface YouTubePlayerBridgeCallbacks {
        /**
         * 播放器监听器集合。
         */
        val listeners: Collection<YouTubePlayerListener>

        /**
         * 获取播放器实例。
         */
        fun getInstance(): YouTubePlayer

        /**
         * IFrame Player API 就绪时的回调。
         */
        fun onYouTubeIFrameAPIReady()
    }

    /**
     * 接收 JavaScript 的 API 就绪事件，分发给播放器。
     */
    @JavascriptInterface
    fun sendYouTubeIFrameAPIReady() = mainThreadHandler.post { youTubePlayerOwner.onYouTubeIFrameAPIReady() }

    /**
     * 接收 JavaScript 的播放器就绪事件，通知所有监听器。
     */
    @JavascriptInterface
    fun sendReady() = mainThreadHandler.post {
        youTubePlayerOwner.listeners.forEach { it.onReady(youTubePlayerOwner.getInstance()) }
    }

    /**
     * 接收 JavaScript 的状态变化事件，解析并分发给监听器。
     * @param state JavaScript 状态字符串
     */
    @JavascriptInterface
    fun sendStateChange(state: String) {
        val playerState = parsePlayerState(state)
        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach { it.onStateChange(youTubePlayerOwner.getInstance(), playerState) }
        }
    }

    /**
     * 接收 JavaScript 的播放质量变化事件，解析并分发给监听器。
     * @param quality JavaScript 质量字符串
     */
    @JavascriptInterface
    fun sendPlaybackQualityChange(quality: String) {
        val playbackQuality = parsePlaybackQuality(quality)
        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach { it.onPlaybackQualityChange(youTubePlayerOwner.getInstance(), playbackQuality) }
        }
    }

    /**
     * 接收 JavaScript 的播放速率变化事件，解析并分发给监听器。
     * @param rate JavaScript 速率字符串
     */
    @JavascriptInterface
    fun sendPlaybackRateChange(rate: String) {
        val playbackRate = parsePlaybackRate(rate)
        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach { it.onPlaybackRateChange(youTubePlayerOwner.getInstance(), playbackRate) }
        }
    }

    /**
     * 解析 JavaScript 状态字符串为 [PlayerConstants.PlayerState]。
     * @param state JavaScript 状态字符串
     * @return 对应的播放器状态
     */
    private fun parsePlayerState(state: String): PlayerConstants.PlayerState {
        return when {
            state.equals(STATE_UNSTARTED, ignoreCase = true) -> PlayerConstants.PlayerState.UNSTARTED
            state.equals(STATE_ENDED, ignoreCase = true) -> PlayerConstants.PlayerState.ENDED
            state.equals(STATE_PLAYING, ignoreCase = true) -> PlayerConstants.PlayerState.PLAYING
            state.equals(STATE_PAUSED, ignoreCase = true) -> PlayerConstants.PlayerState.PAUSED
            state.equals(STATE_BUFFERING, ignoreCase = true) -> PlayerConstants.PlayerState.BUFFERING
            state.equals(STATE_CUED, ignoreCase = true) -> PlayerConstants.PlayerState.VIDEO_CUED
            else -> PlayerConstants.PlayerState.UNKNOWN
        }
    }

    /**
     * 解析 JavaScript 质量字符串为 [PlayerConstants.PlaybackQuality]。
     * @param quality JavaScript 质量字符串
     * @return 对应的播放质量
     */
    private fun parsePlaybackQuality(quality: String): PlayerConstants.PlaybackQuality {
        return when {
            quality.equals(QUALITY_SMALL, ignoreCase = true) -> PlayerConstants.PlaybackQuality.SMALL
            quality.equals(QUALITY_MEDIUM, ignoreCase = true) -> PlayerConstants.PlaybackQuality.MEDIUM
            quality.equals(QUALITY_LARGE, ignoreCase = true) -> PlayerConstants.PlaybackQuality.LARGE
            quality.equals(QUALITY_HD720, ignoreCase = true) -> PlayerConstants.PlaybackQuality.HD720
            quality.equals(QUALITY_HD1080, ignoreCase = true) -> PlayerConstants.PlaybackQuality.HD1080
            quality.equals(QUALITY_HIGH_RES, ignoreCase = true) -> PlayerConstants.PlaybackQuality.HIGH_RES
            quality.equals(QUALITY_DEFAULT, ignoreCase = true) -> PlayerConstants.PlaybackQuality.DEFAULT
            else -> PlayerConstants.PlaybackQuality.UNKNOWN
        }
    }

    /**
     * 解析 JavaScript 速率字符串为 [PlayerConstants.PlaybackRate]。
     * @param rate JavaScript 速率字符串
     * @return 对应的播放速率
     */
    private fun parsePlaybackRate(rate: String): PlayerConstants.PlaybackRate {
        return when {
            rate.equals(RATE_0_25, ignoreCase = true) -> PlayerConstants.PlaybackRate.RATE_0_25
            rate.equals(RATE_0_5, ignoreCase = true) -> PlayerConstants.PlaybackRate.RATE_0_5
            rate.equals(RATE_0_75, ignoreCase = true) -> PlayerConstants.PlaybackRate.RATE_0_75
            rate.equals(RATE_1, ignoreCase = true) -> PlayerConstants.PlaybackRate.RATE_1
            rate.equals(RATE_1_25, ignoreCase = true) -> PlayerConstants.PlaybackRate.RATE_1_25
            rate.equals(RATE_1_5, ignoreCase = true) -> PlayerConstants.PlaybackRate.RATE_1_5
            rate.equals(RATE_1_75, ignoreCase = true) -> PlayerConstants.PlaybackRate.RATE_1_75
            rate.equals(RATE_2, ignoreCase = true) -> PlayerConstants.PlaybackRate.RATE_2
            else -> PlayerConstants.PlaybackRate.UNKNOWN
        }
    }

    /**
     * 解析 JavaScript 错误代码为 [PlayerConstants.PlayerError]。
     * @param error JavaScript 错误代码
     * @return 对应的错误类型
     */
    private fun parsePlayerError(error: String): PlayerConstants.PlayerError {
        return when {
            error.equals(ERROR_INVALID_PARAMETER_IN_REQUEST, ignoreCase = true) -> PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST
            error.equals(ERROR_HTML_5_PLAYER, ignoreCase = true) -> PlayerConstants.PlayerError.HTML_5_PLAYER
            error.equals(ERROR_VIDEO_NOT_FOUND, ignoreCase = true) -> PlayerConstants.PlayerError.VIDEO_NOT_FOUND
            error.equals(ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER1, ignoreCase = true) -> PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER
            error.equals(ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER2, ignoreCase = true) -> PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER
            error.equals(ERROR_REQUEST_MISSING_HTTP_REFERER, ignoreCase = true) -> PlayerConstants.PlayerError.REQUEST_MISSING_HTTP_REFERER
            else -> PlayerConstants.PlayerError.UNKNOWN
        }
    }
}