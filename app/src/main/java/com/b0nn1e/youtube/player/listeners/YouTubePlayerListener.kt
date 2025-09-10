package com.b0nn1e.youtube.player.listeners

import com.b0nn1e.youtube.player.PlayerConstants
import com.b0nn1e.youtube.player.YouTubePlayer

/**
 * 用于监听 YouTube 播放器各种事件的接口。
 * 实现此接口以处理播放器状态、进度、错误等事件。
 */
interface YouTubePlayerListener {
    /**
     * 当播放器准备好播放视频时调用。在此方法调用前，不应操作播放器。
     * @param youTubePlayer 播放器实例
     */
    fun onReady(youTubePlayer: YouTubePlayer)

    /**
     * 当播放器状态变化时调用。可参考 [PlayerConstants.PlayerState] 查看所有可能状态。
     * @param youTubePlayer 播放器实例
     * @param state 播放器状态
     */
    fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState)

    /**
     * 当播放质量变化时调用。可参考 [PlayerConstants.PlaybackQuality] 查看所有可能值。
     * @param youTubePlayer 播放器实例
     * @param playbackQuality 播放质量
     */
    fun onPlaybackQualityChange(
        youTubePlayer: YouTubePlayer,
        playbackQuality: PlayerConstants.PlaybackQuality
    )

    /**
     * 当播放速率变化时调用。可参考 [PlayerConstants.PlaybackRate] 查看所有可能值。
     * @param youTubePlayer 播放器实例
     * @param playbackRate 播放速率
     */
    fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, playbackRate: PlayerConstants.PlaybackRate)

    /**
     * 当播放器发生错误时调用。可参考 [PlayerConstants.PlayerError] 查看所有可能值。
     * @param youTubePlayer 播放器实例
     * @param error 错误类型
     */
    fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError)

    /**
     * 定期调用，报告视频当前播放的秒数。
     * @param youTubePlayer 播放器实例
     * @param second 当前播放秒数
     */
    fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float)

    /**
     * 当视频总时长加载完成时调用。
     * 注意：在视频元数据加载完成前（通常在视频开始播放后），getDuration() 将返回 0。
     * @param youTubePlayer 播放器实例
     * @param duration 视频总时长（秒）
     */
    fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float)

    /**
     * 定期调用，报告视频已缓冲的百分比。
     * @param youTubePlayer 播放器实例
     * @param loadedFraction 已缓冲的视频比例（0.0 到 1.0）
     */
    fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float)

    /**
     * 当当前视频的 ID 加载完成时调用。
     * @param youTubePlayer 播放器实例
     * @param videoId 正在播放的视频 ID
     */
    fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String)

    /**
     * 当播放器 API 发生变化时调用。
     * @param youTubePlayer 播放器实例
     */
    fun onApiChange(youTubePlayer: YouTubePlayer)
}