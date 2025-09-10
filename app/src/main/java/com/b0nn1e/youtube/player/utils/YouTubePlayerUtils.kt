package com.b0nn1e.youtube.player.utils

import androidx.lifecycle.Lifecycle
import com.b0nn1e.youtube.player.YouTubePlayer

/**
 * 根据情况调用 [YouTubePlayer.cueVideo] 或 [YouTubePlayer.loadVideo]。
 * 如果无法判断，默认调用 [YouTubePlayer.cueVideo]。
 *
 * 通常应避免在 Activity/Fragment 不在前台时调用 [YouTubePlayer.loadVideo]。
 * 此函数自动处理生命周期检查，确保视频加载行为适当。
 * @param lifecycle 包含 YouTubePlayerView 的 Activity 或 Fragment 的生命周期
 * @param videoId 视频 ID
 * @param startSeconds 视频开始播放的时间（秒）
 */
fun YouTubePlayer.loadOrCueVideo(lifecycle: Lifecycle, videoId: String, startSeconds: Float) {
    loadOrCueVideo(lifecycle.currentState == Lifecycle.State.RESUMED, videoId, startSeconds)
}

/**
 * 根据 [canLoad] 决定调用 [YouTubePlayer.loadVideo] 或 [YouTubePlayer.cueVideo]。
 * @param canLoad 是否可以立即加载并播放视频（通常表示 Activity/Fragment 在前台）
 * @param videoId 视频 ID
 * @param startSeconds 视频开始播放的时间（秒）
 */
@JvmSynthetic
internal fun YouTubePlayer.loadOrCueVideo(canLoad: Boolean, videoId: String, startSeconds: Float) {
    if (canLoad) {
        loadVideo(videoId, startSeconds)
    } else {
        cueVideo(videoId, startSeconds)
    }
}