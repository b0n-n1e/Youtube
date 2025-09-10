package com.b0nn1e.youtube.player.utils

import com.b0nn1e.youtube.player.PlayerConstants
import com.b0nn1e.youtube.player.YouTubePlayer
import com.b0nn1e.youtube.player.listeners.AbstractYouTubePlayerListener

/**
 * 用于跟踪 YouTube 播放器状态的工具类。
 * 作为 [YouTubePlayerListener] 的实现，需将其添加到 [YouTubePlayer] 的监听器列表才能生效。
 * 跟踪内容包括播放状态、当前播放时间、视频总时长和视频 ID。
 */
class YouTubePlayerTracker : AbstractYouTubePlayerListener() {
    /**
     * 当前播放器状态，来自 [PlayerConstants.PlayerState]，初始为 UNKNOWN。
     */
    var state: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN
        private set

    /**
     * 当前播放时间（秒）。
     */
    var currentSecond: Float = 0f
        private set

    /**
     * 视频总时长（秒）。
     */
    var videoDuration: Float = 0f
        private set

    /**
     * 当前视频 ID，可能为空。
     */
    var videoId: String? = null
        private set

    /**
     * 监听播放器状态变化，更新 [state]。
     * @param youTubePlayer 播放器实例
     * @param state 播放器状态
     */
    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        this.state = state
    }

    /**
     * 监听播放进度，更新 [currentSecond]。
     * @param youTubePlayer 播放器实例
     * @param second 当前播放秒数
     */
    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        currentSecond = second
    }

    /**
     * 监听视频总时长，更新 [videoDuration]。
     * @param youTubePlayer 播放器实例
     * @param duration 视频总时长（秒）
     */
    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        videoDuration = duration
    }

    /**
     * 监听视频 ID，更新 [videoId]。
     * @param youTubePlayer 播放器实例
     * @param videoId 正在播放的视频 ID
     */
    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
        this.videoId = videoId
    }
}