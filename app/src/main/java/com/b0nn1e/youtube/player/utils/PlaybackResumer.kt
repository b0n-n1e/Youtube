package com.b0nn1e.youtube.player.utils

import com.b0nn1e.youtube.player.PlayerConstants
import com.b0nn1e.youtube.player.YouTubePlayer
import com.b0nn1e.youtube.player.listeners.AbstractYouTubePlayerListener

/**
 * 负责在网络问题发生时恢复播放状态的类。
 * 例如：播放器正在播放 -> 网络断开 -> 播放器停止 -> 网络恢复 -> 播放器自动恢复播放。
 * 通常与 [NetworkObserver] 配合使用，监听网络状态变化以触发恢复。
 */
internal class PlaybackResumer : AbstractYouTubePlayerListener() {

    /**
     * 是否允许加载视频，与生命周期相关。
     */
    private var canLoad = false

    /**
     * 播放器是否正在播放。
     */
    private var isPlaying = false

    /**
     * 记录的播放器错误，仅关注 HTML_5_PLAYER 错误。
     */
    private var error: PlayerConstants.PlayerError? = null

    /**
     * 当前播放的视频 ID。
     */
    private var currentVideoId: String? = null

    /**
     * 当前播放进度（秒）。
     */
    private var currentSecond: Float = 0f

    /**
     * 恢复播放状态。
     * 如果存在 HTML_5_PLAYER 错误，根据播放状态加载或预加载视频。
     * @param youTubePlayer 播放器实例
     */
    fun resume(youTubePlayer: YouTubePlayer) {
        val videoId = currentVideoId ?: return
        if (isPlaying && error == PlayerConstants.PlayerError.HTML_5_PLAYER) {
            youTubePlayer.loadOrCueVideo(canLoad, videoId, currentSecond)
        } else if (!isPlaying && error == PlayerConstants.PlayerError.HTML_5_PLAYER) {
            youTubePlayer.cueVideo(videoId, currentSecond)
        }
        error = null
    }

    /**
     * 监听播放器状态变化，更新 [isPlaying] 状态。
     * @param youTubePlayer 播放器实例
     * @param state 播放器状态
     */
    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        when (state) {
            PlayerConstants.PlayerState.ENDED, PlayerConstants.PlayerState.PAUSED -> isPlaying = false
            PlayerConstants.PlayerState.PLAYING -> isPlaying = true
            else -> { }
        }
    }

    /**
     * 监听播放器错误，仅记录 HTML_5_PLAYER 错误。
     * @param youTubePlayer 播放器实例
     * @param error 错误类型
     */
    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
        if (error == PlayerConstants.PlayerError.HTML_5_PLAYER) {
            this.error = error
        }
    }

    /**
     * 更新当前播放进度。
     * @param youTubePlayer 播放器实例
     * @param second 当前播放秒数
     */
    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        currentSecond = second
    }

    /**
     * 更新当前视频 ID。
     * @param youTubePlayer 播放器实例
     * @param videoId 正在播放的视频 ID
     */
    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
        currentVideoId = videoId
    }

    /**
     * 响应生命周期恢复事件，允许加载视频。
     */
    fun onLifecycleResume() {
        canLoad = true
    }

    /**
     * 响应生命周期停止事件，禁止加载视频。
     */
    fun onLifecycleStop() {
        canLoad = false
    }
}