package com.b0nn1e.youtube.player

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import androidx.annotation.RestrictTo
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * 用于从 JavaScript 提取布尔值并传递给 YouTubePlayer 的桥接类。
 * 主要处理 IFrame Player API 的回调，如静音状态。
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class YouTubePlayerCallbacks {
    /**
     * 主线程 Handler，确保回调在主线程执行。
     */
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

    /**
     * 存储布尔值回调的线程安全映射，键为请求 ID。
     */
    private val booleanCallbacks = ConcurrentHashMap<Long, BooleanProvider>()

    /**
     * 用于生成唯一请求 ID 的计数器。
     */
    private val requestId = AtomicLong(0)

    /**
     * 注册一个回调，用于接收 JavaScript 返回的布尔值。
     * @param callback 布尔值回调，典型用途如获取静音状态
     * @return 请求 ID
     */
    fun registerBooleanCallback(callback: BooleanProvider): Long {
        val requestId = requestId.incrementAndGet()
        booleanCallbacks[requestId] = callback
        return requestId
    }

    /**
     * 接收 JavaScript 的布尔值回调，移除并执行对应回调。
     * @param requestId 请求 ID
     * @param value 布尔值
     */
    @JavascriptInterface
    fun sendBooleanValue(requestId: Long, value: Boolean) {
        mainThreadHandler.post {
            val callback = booleanCallbacks.remove(requestId)
            callback?.accept(value)
        }
    }
}