package com.b0nn1e.youtube.player

import com.b0nn1e.youtube.player.listeners.YouTubePlayerListener

/**
 * 用于控制 YouTube 视频播放和监听相关事件的接口。
 * 通常与 [YouTubePlayerView] 结合使用，控制播放器行为。
 */
interface YouTubePlayer {
    /**
     * 加载并自动播放视频。
     * @param videoId 视频 ID
     * @param startSeconds 视频开始播放的时间（秒）
     */
    fun loadVideo(videoId: String, startSeconds: Float)

    /**
     * 加载视频缩略图并准备播放，但不自动播放。
     * @param videoId 视频 ID
     * @param startSeconds 视频开始播放的时间（秒）
     */
    fun cueVideo(videoId: String, startSeconds: Float)

    /**
     * 播放当前视频。
     */
    fun play()

    /**
     * 暂停当前视频。
     */
    fun pause()

    /**
     * 如果播放的是播放列表，播放下一视频。
     */
    fun nextVideo()

    /**
     * 如果播放的是播放列表，播放上一视频。
     */
    fun previousVideo()

    /**
     * 如果播放的是播放列表，播放指定索引的视频。
     * @param index 播放列表中的视频索引
     */
    fun playVideoAt(index: Int)

    /**
     * 如果播放的是播放列表，启用或禁用循环播放。
     * @param loop 是否启用循环
     */
    fun setLoop(loop: Boolean)

    /**
     * 如果播放的是播放列表，启用或禁用随机播放。
     * @param shuffle 是否启用随机播放
     */
    fun setShuffle(shuffle: Boolean)

    /**
     * 静音播放器。
     */
    fun mute()

    /**
     * 取消静音播放器。
     */
    fun unMute()

    /**
     * 异步获取播放器是否静音。
     * @param callback 回调函数，返回静音状态
     */
    fun isMutedAsync(callback: BooleanProvider)

    /**
     * 设置播放器音量。
     * @param volumePercent 音量百分比（0 到 100 之间）
     */
    fun setVolume(volumePercent: Int)

    /**
     * 跳转到指定播放时间。
     * @param time 目标时间（秒）
     */
    fun seekTo(time: Float)

    /**
     * 设置播放速率。
     * @param playbackRate 播放速率，来自 [PlayerConstants.PlaybackRate]
     */
    fun setPlaybackRate(playbackRate: PlayerConstants.PlaybackRate)

    /**
     * 切换全屏模式。
     * 可能需要将 IFrame Player 的 `origin` 参数设置为 "https://www.youtube.com"。
     */
    fun toggleFullscreen()

    /**
     * 添加播放器监听器。
     * @param listener 监听器
     * @return 是否添加成功
     */
    fun addListener(listener: YouTubePlayerListener): Boolean

    /**
     * 移除播放器监听器。
     * @param listener 监听器
     * @return 是否移除成功
     */
    fun removeListener(listener: YouTubePlayerListener): Boolean
}