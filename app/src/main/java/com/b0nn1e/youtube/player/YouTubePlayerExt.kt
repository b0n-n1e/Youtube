package com.b0nn1e.youtube.player

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 返回播放器是否静音，使用协程转换异步回调。
 * @return true 表示静音，false 表示未静音
 */
suspend fun YouTubePlayer.isMuted(): Boolean = suspendCoroutine { continuation ->
    isMutedAsync { isMuted -> continuation.resume(isMuted) }
}