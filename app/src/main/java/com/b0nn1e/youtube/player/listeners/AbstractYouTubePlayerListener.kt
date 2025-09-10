package com.b0nn1e.youtube.player.listeners

import com.b0nn1e.youtube.player.PlayerConstants
import com.b0nn1e.youtube.player.YouTubePlayer

/**
 * 抽象类，实现了 [YouTubePlayerListener] 接口，提供所有方法的空实现。
 * 继承此类以选择性地重写需要的方法，简化监听器实现。
 */
abstract class AbstractYouTubePlayerListener : YouTubePlayerListener {
    /**
     * 当 YouTube 播放器准备好时调用
     * @param youTubePlayer 播放器实例
     */
    override fun onReady(youTubePlayer: YouTubePlayer) {}

    /**
     * 当播放器状态发生变化时调用
     * @param youTubePlayer 播放器实例
     * @param state 播放器状态（如播放、暂停、缓冲等）
     */
    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {}

    /**
     * 当播放质量发生变化时调用
     * @param youTubePlayer 播放器实例
     * @param playbackQuality 播放质量（如高清、标清等）
     */
    override fun onPlaybackQualityChange(youTubePlayer: YouTubePlayer, playbackQuality: PlayerConstants.PlaybackQuality) {}

    /**
     * 当播放速率发生变化时调用
     * @param youTubePlayer 播放器实例
     * @param playbackRate 播放速率（如 1x、2x 等）
     */
    override fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, playbackRate: PlayerConstants.PlaybackRate) {}

    /**
     * 当播放器发生错误时调用
     * @param youTubePlayer 播放器实例
     * @param error 错误类型
     */
    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {}

    /**
     * 当 API 发生变化时调用
     * @param youTubePlayer 播放器实例
     */
    override fun onApiChange(youTubePlayer: YouTubePlayer) {}

    /**
     * 当视频播放进度更新时调用
     * @param youTubePlayer 播放器实例
     * @param second 当前播放的秒数
     */
    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {}

    /**
     * 当获取到视频总时长时调用
     * @param youTubePlayer 播放器实例
     * @param duration 视频总时长（秒）
     */
    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {}

    /**
     * 当视频加载进度更新时调用
     * @param youTubePlayer 播放器实例
     * @param loadedFraction 已加载的视频比例（0.0 到 1.0）
     */
    override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {}

    /**
     * 当获取到视频 ID 时调用
     * @param youTubePlayer 播放器实例
     * @param videoId 视频的唯一 ID
     */
    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {}
}